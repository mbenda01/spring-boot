# Documentation Technique — Wallet API

Ce document détaille les 8 concepts clés d'architecture, de conception et d'optimisation mis en œuvre dans le projet **Wallet API** (Spring Boot 3 / Java 17).

---

## Table des Matières
1. [PageableDefault vs Pageable](#1-pageabledefault-vs-pageable)
2. [Les Validators Personnalisés (Bean Validation)](#2-les-validators-personnalisés)
3. [@Transactional(readOnly = true) vs @Transactional](#3-transactionalreadonly--true-vs-transactional)
4. [Dépendance Mapper (MapStruct)](#4-dépendance-mapper-mapstruct)
5. [EntityGraph (@EntityGraph & Problème N+1)](#5-entitygraph-et-optimisation-n1)
6. [Chargement du fichier data.sql](#6-chargement-du-fichier-datasql)
7. [@RestControllerAdvice & Formatage des Réponses](#7-restcontrolleradvice--formatage-des-réponses)
8. [Ajout de la Documentation Swagger / OpenAPI](#8-ajout-de-la-documentation-swagger--openapi)
9. [Différence entre @Component, @Configuration et @Bean](#9-différence-entre-component-configuration-et-bean)

---

## 1. PageableDefault vs Pageable

### `Pageable`
`Pageable` est une interface du module **Spring Data** (`org.springframework.data.domain.Pageable`). Elle abstrait la pagination et le tri des requêtes en base de données.
Spring MVC résout automatiquement les paramètres HTTP de requête suivants :
- `page` : Le numéro de la page (indexé à partir de `0`).
- `size` : Le nombre d'éléments par page.
- `sort` : Le champ et la direction du tri (ex : `sort=dateCreation,desc`).

### `@PageableDefault`
L'annotation `@PageableDefault` (`org.springframework.data.web.PageableDefault`) permet de définir des **valeurs par défaut** lorsque le client de l'API HTTP n'envoie pas les paramètres `page`, `size` ou `sort` dans l'URL.

### Exemple de Code
```java
@GetMapping
public ResponseEntity<Page<UserResponseDTO>> listerUtilisateurs(
        @RequestParam(required = false) String nom,
        @RequestParam(required = false) String email,
        @PageableDefault(size = 10, sort = "dateCreation", direction = Sort.Direction.DESC)
        Pageable pageable) {

    return ResponseEntity.ok(userService.listerUtilisateurs(nom, email, pageable));
}
```

- **Sans paramètres HTTP** (`GET /api/users`) : `pageable` aura une taille de 10 éléments, triés par `dateCreation` décroissante (`DESC`).
- **Avec paramètres HTTP** (`GET /api/users?page=2&size=20&sort=nom,asc`) : Les valeurs envoyées par le client remplacent les valeurs par défaut de `@PageableDefault`.

---

## 2. Les Validators Personnalisés

Jakarta Bean Validation (`jakarta.validation`) permet d'étendre le système de validation standard (`@NotNull`, `@NotBlank`, `@Email`) en créant des annotations et des validateurs sur mesure.

Un validateur personnalisé requiert **deux éléments** :

### A. L'annotation de contrainte (`@EmailUnique`)
Définie avec `@Constraint(validatedBy = EmailExistValidator.class)`.

```java
package com.iibs.wallet.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailExistValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailUnique {
    String message() default "Cet email est déjà utilisé par un autre compte.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### B. Le validateur de contrainte (`EmailExistValidator`)
Implémente `ConstraintValidator<EmailUnique, String>`. Déclaré comme composant Spring (`@Component`), il peut injecter des repositories pour vérifier les données directement en base.

```java
package com.iibs.wallet.validators;

import com.iibs.wallet.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailExistValidator implements ConstraintValidator<EmailUnique, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true; // Géré par @NotBlank
        }
        return !userRepository.existsByEmail(email.trim().toLowerCase());
    }
}
```

### C. Utilisation dans un DTO
```java
@Data
public class UserCreationDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @EmailUnique
    private String email;
}
```
Lors de l'appel HTTP `POST /api/users` avec `@Valid`, la validation s'exécute **avant** d'entrer dans la couche service. Si l'email existe déjà, une erreur HTTP `400 Bad Request` est retournée avec le message de validation.

---

## 3. `@Transactional(readOnly = true)` vs `@Transactional`

L'annotation `@Transactional` de Spring (`org.springframework.transaction.annotation.Transactional`) gère le contexte de transaction SQL.

### `@Transactional(readOnly = true)`
- **Niveau Classe** : Appliquée au niveau du service pour définir le comportement par défaut de toutes les méthodes.
- **Optimisation Hibernate / JPA** :
  - **Désactivation du Dirty Checking** : Hibernate ne conserve pas de snapshot des entités pour détecter les modifications, ce qui économise de la mémoire.
  - **Optimisation JDBC** : Le driver de base de données applique un mode lecture seule (`READ ONLY`), permettant à la base de données d'optimiser les verrous et requêtes.
  - **Sécurité** : Empêche la modification accidentelle des données.

### `@Transactional` (Écriture)
- **Niveau Méthode** : Appliquée spécifiquement sur les méthodes qui modifient des données (`creerUtilisateur`, `creerPortefeuille`).
- **Garanties ACID** :
  - Ouvre une transaction en écriture.
  - Valide (`COMMIT`) automatiquement si aucune exception non contrôlée n'est levée.
  - Annule (`ROLLBACK`) en cas de `RuntimeException`.

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 1. Mode lecture seule par défaut
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO trouverParId(Long id) {
        // Exécuté en lecture seule (optimisé)
        return userRepository.findById(id).map(...);
    }

    @Transactional // 2. Surcharge pour les opérations de modification
    public UserResponseDTO creerUtilisateur(UserCreationDTO dto) {
        // Exécuté dans une transaction en écriture avec commit / rollback
        User user = userRepository.save(...);
        return toDTO(user);
    }
}
```

---

## 4. Dépendance Mapper (MapStruct)

Dans une application RESTful moderne, les entités du domaine (`User`, `Wallet`) sont strictement isolées des contrats d'API exposés au client (`UserResponseDTO`, `WalletResponseDTO`). Pour réaliser ces transformations sans écrire de code répétitif manuel (`dto.setNom(user.getNom())`), on utilise **MapStruct**.

---

### A. Pourquoi MapStruct plutôt que ModelMapper ou Dozer ?

| Critère | ModelMapper / Dozer | MapStruct |
|---|---|---|
| **Mécanisme** | Réflexion dynamique à l'exécution (*Runtime Reflection*) | Génération de code Java à la compilation (*Compile-Time APT*) |
| **Performances** | Lent (overhead d'inspection de classes à chaque requête) | **Ultra-rapide** (équivalent à du code écrit à la main, 0 overhead) |
| **Sécurité du typage** | Erreurs de typage/nommage masquées jusqu'à l'exécution | **Détection des erreurs au moment du `mvn compile`** |
| **Débogage** | Difficile (stack traces complexes d'introspection) | **Très facile** (code source Java pur lisible et traçable dans `target/`) |

---

### B. Architecture & Configuration Maven (`pom.xml`)

MapStruct fonctionne via un **Annotation Processor** (APT - *Annotation Processing Tool*) de Java (`javac`). Lors de la phase de compilation, le processeur analyse les interfaces annotées avec `@Mapper` et produit le code source Java réel des implémentations.

#### Synergie entre MapStruct et Lombok
Puisque Lombok génère dynamiquement les `getters`, `setters` et `builders` à la compilation, et que MapStruct a besoin de ces getters/setters pour générer le code de mapping, il est indispensable de configurer l'ordre des processeurs dans le `maven-compiler-plugin` via l'artefact **`lombok-mapstruct-binding`**.

```xml
<properties>
    <java.version>17</java.version>
    <org.mapstruct.version>1.5.5.Final</org.mapstruct.version>
    <org.projectlombok.version>1.18.30</org.projectlombok.version>
    <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>
</properties>

<dependencies>
    <!-- Bibliothèque MapStruct (interfaces & annotations) -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${org.mapstruct.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <annotationProcessorPaths>
                    <!-- 1. Lombok génère d'abord les getters/setters -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${org.projectlombok.version}</version>
                    </path>
                    <!-- 2. Binding pour l'interopérabilité Lombok + MapStruct -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>${lombok-mapstruct-binding.version}</version>
                    </path>
                    <!-- 3. MapStruct génère le code d'implémentation des mappers -->
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${org.mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

### C. Explication Détaillée des Annotations MapStruct

MapStruct fournit un ensemble d'annotations et de directives pour piloter précisément la génération du code :

1. **`@Mapper(componentModel = "spring")`** :
   - Indique à MapStruct que l'implémentation générée doit être annotée avec `@Component` de Spring Framework.
   - Permet d'injecter nativement le mapper via le conteneur Spring IoC (`@Autowired` ou par constructeur).

2. **`@Mapping(source = "...", target = "...")`** :
   - Mappe explicitement un champ d'origine vers un champ de destination quand les noms d'attributs sont différents.
   - **Propriétés Imbriquées (*Nested Properties*)** : Permet la navigation dans le graphe d'objets (ex: `source = "utilisateur.nom"` va chercher `user.getUtilisateur().getNom()` pour remplir `nomUtilisateur`).

3. **`@Mapping(target = "...", ignore = true)`** :
   - Demande à MapStruct de ne pas toucher à ce champ lors de la conversion. Indispensable lors de la création d'une entité à partir d'un DTO (`UserCreationDTO`) pour éviter d'écraser des valeurs gérées par JPA (ex : `id`, `dateCreation`).

4. **`@Named` et Méthodes `default` (Transformations Sur Mesure)** :
   - Permet d'écrire de la logique métier personnalisée en Java pur directement dans l'interface.
   - La directive `qualifiedByName` associe le mapping à cette méthode spécifique.

---

### D. Implémentations des Mappers du Projet

#### 1. `UserMapper.java`
Convertit l'entité `User` en `UserResponseDTO` et le DTO de création `UserCreationDTO` en entité `User`.

```java
package com.iibs.wallet.mapper;

import com.iibs.wallet.dto.UserCreationDTO;
import com.iibs.wallet.dto.UserResponseDTO;
import com.iibs.wallet.entity.User;
import com.iibs.wallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Interface Mapper MapStruct pour la conversion User <-> DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convertit l'entité User en DTO de réponse UserResponseDTO.
     * Mappe la liste des portefeuilles (wallets) vers le nombre entier (nombrePortefeuilles) via mapNombrePortefeuilles.
     */
    @Mapping(target = "nombrePortefeuilles", source = "wallets", qualifiedByName = "mapNombrePortefeuilles")
    UserResponseDTO toDTO(User user);

    /**
     * Convertit le DTO de création UserCreationDTO en entité User.
     * Ignore les champs techniques gérés par JPA (id, dateCreation, wallets).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "wallets", ignore = true)
    User toEntity(UserCreationDTO dto);

    /**
     * Méthode personnalisée : calcule la taille de la liste de portefeuilles en toute sécurité.
     */
    @Named("mapNombrePortefeuilles")
    default int mapNombrePortefeuilles(List<Wallet> wallets) {
        return wallets != null ? wallets.size() : 0;
    }
}
```

#### 2. `WalletMapper.java`
Convertit l'entité `Wallet` en `WalletResponseDTO` en extrayant les informations de l'utilisateur propriétaire lié (`@ManyToOne`).

```java
package com.iibs.wallet.mapper;

import com.iibs.wallet.dto.WalletResponseDTO;
import com.iibs.wallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface Mapper MapStruct pour la conversion Wallet <-> WalletResponseDTO.
 */
@Mapper(componentModel = "spring")
public interface WalletMapper {

    /**
     * Extraction automatique des propriétés de l'entité User liée :
     *   wallet.getUtilisateur().getId()  -> dto.setUtilisateurId(...)
     *   wallet.getUtilisateur().getNom() -> dto.setNomUtilisateur(...)
     */
    @Mapping(target = "utilisateurId", source = "utilisateur.id")
    @Mapping(target = "nomUtilisateur", source = "utilisateur.nom")
    WalletResponseDTO toDTO(Wallet wallet);
}
```

---

### E. Code Généré sous le Capot par MapStruct

Lors de l'exécution de `mvn compile`, MapStruct génère automatiquement les classes Java d'implémentation dans le dossier `target/generated-sources/annotations/com/iibs/wallet/mapper/`.

Voici un aperçu du code Java pur généré par MapStruct pour **`WalletMapperImpl.java`** :

```java
// CODE GÉNÉRÉ AUTOMATIQUEMENT PAR MAPSTRUCT (NE PAS MODIFIER MANUELLEMENT)
package com.iibs.wallet.mapper;

import com.iibs.wallet.dto.WalletResponseDTO;
import com.iibs.wallet.entity.User;
import com.iibs.wallet.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapperImpl implements WalletMapper {

    @Override
    public WalletResponseDTO toDTO(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }

        WalletResponseDTO walletResponseDTO = new WalletResponseDTO();

        // Récupération des propriétés imbriquées gérée de manière sécurisée
        Long id = walletUtilisateurId( wallet );
        if ( id != null ) {
            walletResponseDTO.setUtilisateurId( id );
        }
        String nom = walletUtilisateurNom( wallet );
        if ( nom != null ) {
            walletResponseDTO.setNomUtilisateur( nom );
        }

        walletResponseDTO.setId( wallet.getId() );
        walletResponseDTO.setSolde( wallet.getSolde() );
        walletResponseDTO.setDevise( wallet.getDevise() );
        walletResponseDTO.setDateCreation( wallet.getDateCreation() );

        return walletResponseDTO;
    }

    private Long walletUtilisateurId(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }
        User utilisateur = wallet.getUtilisateur();
        if ( utilisateur == null ) {
            return null;
        }
        return utilisateur.getId();
    }

    private String walletUtilisateurNom(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }
        User utilisateur = wallet.getUtilisateur();
        if ( utilisateur == null ) {
            return null;
        }
        return utilisateur.getNom();
    }
}
```

---

### F. Intégration Native dans la Couche Service Spring

Les mappers générés étant annotés avec `@Component`, ils sont injectés automatiquement dans les services métier via l'annotation Lombok `@RequiredArgsConstructor` :

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper; // Injecté automatiquement par Spring IoC !

    public WalletResponseDTO trouverParId(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portefeuille", id));

        // Utilisation propre et fluide du mapper MapStruct
        return walletMapper.toDTO(wallet);
    }
}
```

---

## 5. EntityGraph et Optimisation N+1

### Le Problème du N+1 Select
Par défaut, les relations `@OneToMany` et `@ManyToOne(fetch = FetchType.LAZY)` sont chargées de manière **paresseuse** (*Lazy Loading*).
Si l'on récupère N portefeuilles et qu'on accède au nom de l'utilisateur pour chaque portefeuille :
1. **1 requêtes SQL** pour lire la liste des N portefeuilles.
2. **N requêtes SQL** individuelles pour lire chaque utilisateur associé.
👉 Résultat : **1 + N requêtes SQL**, dégradant gravement les performances.

### La Solution : `@EntityGraph`
L'annotation `@EntityGraph` de Spring Data JPA permet d'indiquer à JPA de charger la relation spécifiée de manière **EAGER** via une seule jointure SQL (`LEFT JOIN FETCH`).

```java
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Charge les portefeuilles ET l'utilisateur associé en 1 seule requête SQL.
     */
    @Override
    @EntityGraph(attributePaths = {"utilisateur"})
    Page<Wallet> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"utilisateur"})
    Page<Wallet> findByDeviseIgnoreCase(String devise, Pageable pageable);
}
```

### Requête SQL Générée par `@EntityGraph`
```sql
SELECT w.id, w.solde, w.devise, u.id, u.nom, u.email 
FROM wallets w 
LEFT OUTER JOIN utilisateurs u ON w.utilisateur_id = u.id;
```

### Règle d'Optimisation
- **Utiliser `@EntityGraph`** sur les requêtes de listing / projection où l'entité liée est lue (conversion DTO).
- **Ne PAS utiliser `@EntityGraph`** sur les requêtes simples de vérification (ex : `existsByEmail`) afin de ne pas charger inutilement des jointures complexes.

---

## 6. Chargement du fichier `data.sql`

Spring Boot permet de pré-remplir la base de données avec des scripts SQL d'initialisation (`src/main/resources/data.sql`).

### Configuration Dans `application.properties`
```properties
# 1. Spécifie la base de données embarquée H2 en mémoire
spring.datasource.url=jdbc:h2:mem:walletdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE

# 2. Demande à Hibernate de créer le schéma de tables à partir des entités Java
spring.jpa.hibernate.ddl-auto=create-drop

# 3. Diffère l'exécution du script data.sql APRÈS la création des tables par Hibernate
spring.jpa.defer-datasource-initialization=true

# 4. Force l'exécution du script d'initialisation SQL au démarrage
spring.sql.init.mode=always
```

### Ordre d'Exécution au Démarrage
1. Hibernate analyse les classes `@Entity` (`User`, `Wallet`) et génère les tables (`CREATE TABLE`).
2. Spring Boot exécute le fichier `data.sql` pour insérer les jeux de données initiaux (`INSERT INTO`).

---

## 7. `@RestControllerAdvice` & Formatage des Réponses (`ApiResponse`)

Dans une architecture RESTful Spring Boot, `@RestControllerAdvice` est utilisé pour deux rôles complémentaires :
1. **La gestion globale des erreurs** via `GlobalExceptionHandler` (`@ExceptionHandler`).
2. **L'enveloppement et le formatage transparent des réponses réussies** via `GlobalResponseFormatter` (`ResponseBodyAdvice<Object>`) dans des objets **`ApiResponse<T>`**.

---

### A. Intercepteur de Succès : `GlobalResponseFormatter` (`ResponseBodyAdvice`)

`GlobalResponseFormatter` implémente l'interface Spring **`ResponseBodyAdvice<Object>`**. Il intercepte toutes les réponses retournées par les méthodes de contrôleurs (`ResponseEntity<UserResponseDTO>`, `ResponseEntity<Page<WalletResponseDTO>>`, etc.) **juste avant** que Jackson ne les sérialise en JSON.

Il construit automatiquement l'objet DTO **`ApiResponse<T>`** en encapsulant la donnée d'origine sous l'attribut `data`.

#### Code d'implémentation :
```java
package com.iibs.wallet.exception;

import com.iibs.wallet.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Intercepteur global @RestControllerAdvice implémentant ResponseBodyAdvice.
 * Transforme de manière transparente toutes les réponses des contrôleurs en objets ApiResponse.
 */
@RestControllerAdvice(basePackages = "com.iibs.wallet.controller")
public class GlobalResponseFormatter implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // Intercepte tous les contrôleurs du package com.iibs.wallet.controller
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // 1. Évite la double-enveloppe si le corps est déjà une ApiResponse ou un Map d'erreur (provenant de GlobalExceptionHandler)
        if (body instanceof ApiResponse || (body instanceof Map && ((Map<?, ?>) body).containsKey("erreur"))) {
            return body;
        }

        // 2. Extrait le statut HTTP de la réponse (200 OK, 201 Created, etc.)
        int statusCode = 200;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            statusCode = servletResponse.getServletResponse().getStatus();
        }

        // 3. Enveloppe les données métiers dans un objet ApiResponse
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .statut(statusCode)
                .succes(true)
                .message("Opération réalisée avec succès")
                .data(body)
                .build();
    }
}
```

#### Code du Contrôleur (Exemple `UserController.java`) :
Les contrôleurs restent complètement **épurés** et ne contiennent aucune référence explicite à `ApiResponse` :
```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> trouverParId(@PathVariable Long id) {
        // Le contrôleur retourne simplement le DTO — GlobalResponseFormatter s'occupe de l'enveloppe ApiResponse
        return ResponseEntity.ok(userService.trouverParId(id));
    }
}
```

#### Détail du fonctionnement :
- **`supports(...)`** : Détermine si l'intercepteur s'applique au contrôleur. En retournant `true`, il s'active pour tous les endpoints REST.
- **`beforeBodyWrite(...)`** : Reçoit le corps original retourné par le contrôleur (`UserResponseDTO`, `Page<WalletResponseDTO>`, etc.). Il extrait le statut HTTP réel (`200`, `201`) et construit l'objet **`ApiResponse`**.
- **Avantage majeur** : Découplage total — les contrôleurs REST restent simples et testables en retournant des DTOs purs sans dépendre de l'enveloppe globale.

#### Exemple de Réponse JSON de Succès Générée (200 OK) :
```json
{
  "timestamp": "2026-07-24T19:47:00.123456",
  "statut": 200,
  "succes": true,
  "message": "Opération réalisée avec succès",
  "data": {
    "id": 1,
    "nom": "Amadou Diallo",
    "email": "amadou.diallo@example.com",
    "dateCreation": "2026-07-24T19:23:52.449922",
    "nombrePortefeuilles": 2
  }
}
```

---

### B. Gestionnaire Global d'Exceptions : `GlobalExceptionHandler`

`GlobalExceptionHandler` intercepte les exceptions levées dans n'importe quelle couche de l'application et les transforme en réponses JSON structurées.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400 Bad Request — Erreurs de validation DTO (@Valid / @EmailUnique)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String champ = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            details.put(champ, message);
        });

        Map<String, Object> corps = new HashMap<>();
        corps.put("timestamp", LocalDateTime.now());
        corps.put("statut", HttpStatus.BAD_REQUEST.value());
        corps.put("erreur", "Erreur de validation");
        corps.put("details", details);

        return ResponseEntity.badRequest().body(corps);
    }

    /**
     * 404 Not Found — Ressource introuvable
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * 409 Conflict — Conflit de données (doublons)
     */
    @ExceptionHandler(ConflitDonneesException.class)
    public ResponseEntity<Map<String, Object>> handleConflit(ConflitDonneesException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        Map<String, Object> corps = new HashMap<>();
        corps.put("timestamp", LocalDateTime.now());
        corps.put("statut", status.value());
        corps.put("erreur", status.getReasonPhrase());
        corps.put("message", message);
        return ResponseEntity.status(status).body(corps);
    }
}
```

#### Exemple de Réponse JSON d'Erreur (400 Bad Request) :
```json
{
  "timestamp": "2026-07-24T19:30:00.123",
  "statut": 400,
  "erreur": "Erreur de validation",
  "details": {
    "email": "Cet email est déjà utilisé par un autre compte.",
    "nom": "Le nom est obligatoire"
  }
}
```

---

## 8. Ajout de la Documentation Swagger / OpenAPI

Springdoc OpenAPI permet de générer automatiquement une documentation interactive REST (Swagger UI) à partir des contrôleurs Spring MVC.

### A. Dépendance Maven (`pom.xml`)
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

### B. Configuration OpenAPI avec Multi-Hôtes (`OpenApiConfig.java`)
```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI walletOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Environnement Local (Développement)");

        Server stagingServer = new Server()
                .url("https://api-staging.wallet.iibs.edu")
                .description("Environnement de Staging / Recette");

        return new OpenAPI()
                .info(new Info()
                        .title("Wallet API — Gestion de Portefeuilles Numériques")
                        .description("API RESTful Spring Boot pour la gestion des portefeuilles numériques")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("IIBS")
                                .email("contact@iibs.edu")))
                .servers(List.of(localServer, stagingServer));
    }
}
```

### C. Annotations dans les Contrôleurs REST
- `@Tag` : Regroupe les endpoints par module fonctionnel.
- `@Operation` : Décrit le rôle d'un endpoint et ses réponses HTTP.
- `@Parameter` : Décrit un paramètre d'URL (`@PathVariable`, `@RequestParam`).

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs du système")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(
        summary = "Lister les utilisateurs",
        description = "Retourne la liste paginée des utilisateurs avec filtres optionnels sur nom et email."
    )
    public ResponseEntity<Page<UserResponseDTO>> listerUtilisateurs(
            @Parameter(description = "Filtre partiel sur le nom") @RequestParam(required = false) String nom,
            @Parameter(description = "Filtre partiel sur l'email") @RequestParam(required = false) String email,
            @PageableDefault(size = 10, sort = "dateCreation", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(userService.listerUtilisateurs(nom, email, pageable));
    }
}
```

### D. Accès aux Interfaces Swagger
Une fois le projet démarré (`mvn spring-boot:run`) :
- **Interface Graphique Swagger UI** : `http://localhost:8080/swagger-ui.html`
- **Spécification OpenAPI (JSON)** : `http://localhost:8080/api-docs`

---

## 9. Différence entre `@Component`, `@Configuration` et `@Bean`

En Spring, l'inversion de contrôle (IoC) et l'injection de dépendances reposent sur la gestion de **beans**. Spring propose plusieurs annotations pour déclarer et instancier ces beans dans l'ApplicationContext.

---

### A. `@Component`

- **Cible** : Classe (`ElementType.TYPE`).
- **Principe** : Indique à Spring de détecter automatiquement cette classe lors du balayage de composants (*Component Scanning*) et d'en créer une instance unique (Singleton par défaut) gérée dans le conteneur IoC.
- **Utilisation** : S'applique sur les **classes de votre propre code source**.
- **Stéréotypes spécialisés** : `@Component` est la méta-annotation de base dont dérivent :
  - `@Service` : Pour les classes de logique métier.
  - `@Repository` : Pour la couche d'accès aux données (traduit automatiquement les exceptions SQL en exceptions Spring).
  - `@Controller` / `@RestController` : Pour les contrôleurs Web REST.
  - `@RestControllerAdvice` : Pour l'interception globale des réponses et des exceptions.

#### Exemple dans le projet :
```java
@Component
@RequiredArgsConstructor
public class EmailExistValidator implements ConstraintValidator<EmailUnique, String> {
    private final UserRepository userRepository;
    // ...
}
```

---

### B. `@Configuration`

- **Cible** : Classe (`ElementType.TYPE`).
- **Principe** : Indique que la classe contient une ou plusieurs méthodes annotées `@Bean`. C'est l'équivalent moderne en code Java des anciens fichiers XML de configuration Spring.
- **Spécificité (Proxy CGLIB)** : Spring entoure les classes `@Configuration` d'un **proxy CGLIB**. Ce proxy intercepte les appels entre méthodes `@Bean` de la classe pour garantir qu'un seul et unique bean Singleton est instancié, même si la méthode est appelée plusieurs fois.
- **Utilisation** : Pour regrouper la configuration et la création d'instances d'objets complexes ou externes.

#### Exemple dans le projet :
```java
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
```

---

### C. `@Bean`

- **Cible** : Méthode (`ElementType.METHOD`).
- **Principe** : Utilisée exclusivement **à l'intérieur d'une classe de configuration** (`@Configuration`). Indique à Spring que la valeur de retour de cette méthode doit être enregistrée en tant que Bean Spring dans le conteneur IoC.
- **Utilisation** : 
  1. Pour instancier et configurer des **bibliothèques tierces / externes** (dont vous n'avez pas le code source pour y ajouter `@Component`, ex : `ModelMapper`, `OpenAPI`).
  2. Pour personnaliser manuellement l'initialisation d'un objet via un constructeur ou des setters.

#### Exemple dans le projet :
```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI walletOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wallet API — Gestion de Portefeuilles Numériques")
                        .version("1.0.0"));
    }
}
```

---

### D. Tableau Comparatif Synthétique

| Critère | `@Component` | `@Configuration` | `@Bean` |
|---|---|---|---|
| **Cible** | Classe (`ElementType.TYPE`) | Classe (`ElementType.TYPE`) | Méthode (`ElementType.METHOD`) |
| **Détection** | Automatique (*Component Scan*) | Automatique (*Component Scan*) | Manuelle (Méthode dans `@Configuration`) |
| **Cas d'usage principal** | Vos propres classes (Services, Mappers, Validateurs) | Regrouper les configurations Java de l'application | Instancier des objets tiers (`ModelMapper`, `OpenAPI`, `DataSource`) |
| **Proxy CGLIB** | Non (appel direct de méthodes) | Oui (garantit l'unicité Singleton des `@Bean`) | N/A |
| **Accès au code source** | Requis (pour annoter la classe) | Requis (pour créer la classe de config) | Non requis (instancie des classes externes) |

