# Document de Sécurité des API REST

Ce document présente les concepts fondamentaux, les meilleures pratiques et les lignes directrices pour concevoir, implémenter et maintenir des API REST sécurisées.

## 1. Authentification et Autorisation

Ces deux concepts sont les fondations de toute application sécurisée. Il est crucial de bien les distinguer.

| Concept | Définition | Exemple |
| :--- | :--- | :--- |
| **Authentification** | Qui es-tu ? — Vérifie l'identité | Login avec email / mot de passe |
| **Autorisation** | Que peux-tu faire ? — Vérifie les droits | `ADMIN` peut supprimer un utilisateur |

---

## 2. Architectures d'Authentification

Lors de la conception d'une API, plusieurs approches existent pour authentifier les utilisateurs et gérer leur état.

### 2.1 Authentification Basique (Basic Auth)

C'est la méthode la plus simple. Le client envoie les identifiants à chaque requête.

> [!WARNING]
> **Ne jamais utiliser sans HTTPS :** Les identifiants sont envoyés en clair (simplement encodés en Base64) à chaque requête. S'ils sont interceptés sur un réseau non chiffré, ils sont lisibles immédiatement.

![Diagramme de séquence](https://mermaid.ink/img/eyJjb2RlIjogInNlcXVlbmNlRGlhZ3JhbVxuICAgIHBhcnRpY2lwYW50IEMgYXMgQ2xpZW50XG4gICAgcGFydGljaXBhbnQgUyBhcyBTZXJ2ZXVyXG4gICAgcGFydGljaXBhbnQgREIgYXMgQmFzZSBkZSBEb25uXHUwMGU5ZXNcblxuICAgIEMtPj5TOiAxLiBSZXF1XHUwMGVhdGUgR0VUIC9yZXNzb3VyY2VcbiAgICBTLS0-PkM6IDIuIDQwMSBVbmF1dGhvcml6ZWQgKFdXVy1BdXRoZW50aWNhdGU6IEJhc2ljKVxuICAgIE5vdGUgb3ZlciBDOiBFbmNvZGUgXCJlbWFpbDptb3RfZGVfcGFzc2VcIiBlbiBCYXNlNjRcbiAgICBDLT4-UzogMy4gR0VUIC9yZXNzb3VyY2UgKEF1dGhvcml6YXRpb246IEJhc2ljIGRYTmxjanB3WVhOeilcbiAgICBTLT4-REI6IDQuIFZcdTAwZTlyaWZpZSBsZXMgaWRlbnRpZmlhbnRzXG4gICAgREItLT4-UzogNS4gSWRlbnRpZmlhbnRzIHZhbGlkZXNcbiAgICBTLS0-PkM6IDYuIDIwMCBPSyAoRG9ublx1MDBlOWVzKSIsICJtZXJtYWlkIjogIntcInRoZW1lXCI6IFwiZGVmYXVsdFwiLCBcImJhY2tncm91bmRDb2xvclwiOiBcIndoaXRlXCJ9In0=)

### 2.2 Flux d'Authentification Stateful (Basé sur la session)

Dans ce modèle, le serveur doit se souvenir du client entre chaque requête.

![Diagramme de séquence](https://mermaid.ink/img/eyJjb2RlIjogInNlcXVlbmNlRGlhZ3JhbVxuICAgIHBhcnRpY2lwYW50IEMgYXMgQ2xpZW50XG4gICAgcGFydGljaXBhbnQgUyBhcyBTZXJ2ZXVyXG4gICAgcGFydGljaXBhbnQgREIgYXMgQmFzZSBkZSBEb25uXHUwMGU5ZXNcblxuICAgIEMtPj5TOiAxLiBQT1NUIC9sb2dpbiAoZW1haWwsIG1vdCBkZSBwYXNzZSlcbiAgICBTLT4-REI6IDIuIFZcdTAwZTlyaWZpZSBsZXMgaWRlbnRpZmlhbnRzXG4gICAgREItLT4-UzogMy4gSWRlbnRpZmlhbnRzIHZhbGlkZXNcbiAgICBTLT4-UzogNC4gQ3JcdTAwZTllIHVuZSBzZXNzaW9uIChlbiBtXHUwMGU5bW9pcmUgb3UgREIpXG4gICAgUy0tPj5DOiA1LiAyMDAgT0sgKyBTZXQtQ29va2llOiBKU0VTU0lPTklEPWFiYzEyM3h5elxuICAgIFxuICAgIE5vdGUgb3ZlciBDLCBTOiBMb3JzIGRlIGxhIHJlcXVcdTAwZWF0ZSBzdWl2YW50ZS4uLlxuICAgIFxuICAgIEMtPj5TOiA2LiBHRVQgL3Byb2ZpbCAoQ29va2llOiBKU0VTU0lPTklEPWFiYzEyM3h5eilcbiAgICBTLT4-UzogNy4gQ2hlcmNoZSBsYSBzZXNzaW9uICdhYmMxMjN4eXonIGVuIG1cdTAwZTltb2lyZS9EQlxuICAgIFMtLT4-QzogOC4gMjAwIE9LIChEb25uXHUwMGU5ZXMgZHUgcHJvZmlsKSIsICJtZXJtYWlkIjogIntcInRoZW1lXCI6IFwiZGVmYXVsdFwiLCBcImJhY2tncm91bmRDb2xvclwiOiBcIndoaXRlXCJ9In0=)

### 2.3 Flux d'Authentification Stateless (Basé sur le Token / JWT)

Dans ce modèle, le serveur ne retient rien. Le client fournit toutes les preuves nécessaires à chaque requête.

![Diagramme de séquence](https://mermaid.ink/img/eyJjb2RlIjogInNlcXVlbmNlRGlhZ3JhbVxuICAgIHBhcnRpY2lwYW50IEMgYXMgQ2xpZW50XG4gICAgcGFydGljaXBhbnQgUyBhcyBTZXJ2ZXVyXG4gICAgcGFydGljaXBhbnQgREIgYXMgQmFzZSBkZSBEb25uXHUwMGU5ZXNcblxuICAgIEMtPj5TOiAxLiBQT1NUIC9sb2dpbiAoZW1haWwsIG1vdCBkZSBwYXNzZSlcbiAgICBTLT4-REI6IDIuIFZcdTAwZTlyaWZpZSBsZXMgaWRlbnRpZmlhbnRzXG4gICAgREItLT4-UzogMy4gSWRlbnRpZmlhbnRzIHZhbGlkZXNcbiAgICBTLT4-UzogNC4gR1x1MDBlOW5cdTAwZThyZSBldCBzaWduZSB1biBKV1QgKGF2ZWMgdW4gc2VjcmV0KVxuICAgIFMtLT4-QzogNS4gMjAwIE9LICsgeyBcInRva2VuXCI6IFwiZXlKaGJHLi4uXCIgfVxuICAgIFxuICAgIE5vdGUgb3ZlciBDLCBTOiBMb3JzIGRlIGxhIHJlcXVcdTAwZWF0ZSBzdWl2YW50ZS4uLlxuICAgIFxuICAgIEMtPj5TOiA2LiBHRVQgL3Byb2ZpbCAoQXV0aG9yaXphdGlvbjogQmVhcmVyIGV5SmhiRy4uLilcbiAgICBTLT4-UzogNy4gVlx1MDBlOXJpZmllIGxhIHNpZ25hdHVyZSBkdSBKV1QgKHNhbnMgREIpXG4gICAgUy0tPj5DOiA4LiAyMDAgT0sgKERvbm5cdTAwZTllcyBkdSBwcm9maWwpIiwgIm1lcm1haWQiOiAie1widGhlbWVcIjogXCJkZWZhdWx0XCIsIFwiYmFja2dyb3VuZENvbG9yXCI6IFwid2hpdGVcIn0ifQ==)

### 2.4 Comparaison : Basique vs Stateful vs Stateless

Le choix du mécanisme d'authentification a un impact direct sur la sécurité, les performances et l'architecture de votre API.

| Caractéristique | Basique (Basic Auth) | Stateful (Session) | Stateless (JWT) |
| :--- | :--- | :--- | :--- |
| **Stockage serveur** | Aucun stockage d'état de session. | Stocke la session (mémoire, BDD, Redis). | Aucun (l'état est stocké dans le token). |
| **Transmission** | Identifiants (`email:pass`) envoyés à *chaque* requête. | Session ID envoyé via un Cookie (`JSESSIONID`). | Token envoyé via l'en-tête `Authorization`. |
| **Sécurité** | Faible (identifiants exposés en continu, nécessite HTTPS absolu). | Moyenne/Haute (vulnérable CSRF, mais masque les identifiants). | Haute (si secret fort, mais token vulnérable au vol XSS). |
| **Performance DB** | Mauvaise (doit vérifier le mot de passe en base à chaque appel). | Moyenne (doit chercher la session en base ou en cache). | Excellente (vérification de la signature cryptographique sans DB). |
| **Scalabilité** | Excellente (Serveur stateless). | Difficile (nécessite un cache distribué ou des *Sticky Sessions*). | Native (Le serveur ne retient rien). |
| **Cas d'usage** | Scripts internes simples, systèmes "legacy". | Applications Web classiques (Monolithes). | Microservices, APIs REST modernes, SPA (React, Angular). |

> [!WARNING]
> **Δ Éviter en monolithique modulaire :** L'approche Stateful pose problème : quelle instance détient la session ? Privilégiez l'approche Stateless pour les APIs.

---

## 3. JSON Web Token (JWT) en Détail

Un JWT (RFC 7519) est le format standard pour l'authentification Stateless. Il permet d'échanger des informations de manière sécurisée sous forme d'objet JSON. 

Il est composé de trois parties distinctes, séparées par des points (`.`), et encodées en Base64Url : `Header.Payload.Signature`

```text
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9          ← HEADER (En-tête)
.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZXMi  ← PAYLOAD (Données / Claims)
.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c   ← SIGNATURE (Preuve d'intégrité)
```

### 3.1 Le Header (En-tête)

Le header définit la nature du token et comment il est sécurisé. Il contient généralement deux propriétés :
*   `typ` (Type) : Toujours défini à "JWT".
*   `alg` (Algorithme) : L'algorithme utilisé pour la signature, tel que HMAC SHA256 (HS256) ou RSA (RS256).

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### 3.2 Le Payload (Données ou "Claims")

C'est le cœur du token. Il contient les *claims* (déclarations), qui sont des informations sur une entité (souvent l'utilisateur) et des métadonnées additionnelles.

Il existe trois types de claims :
1.  **Registered claims (Déclarations enregistrées) :** Prédéfinies par le standard, elles ne sont pas obligatoires mais recommandées pour l'interopérabilité.
    *   `iss` (Issuer) : L'émetteur du token.
    *   `exp` (Expiration) : Date d'expiration (timestamp Unix). Un token expiré doit être rejeté.
    *   `sub` (Subject) : Le sujet du token (ex: l'ID de l'utilisateur).
    *   `iat` (Issued At) : Date d'émission du token.
2.  **Public claims :** Définies par les développeurs. Pour éviter les conflits de nommage, elles doivent être enregistrées au registre IANA ou sous forme d'URI.
3.  **Private claims :** Informations personnalisées échangées entre le serveur et le client (ex: `roles`, `isAdmin`, `tenant_id`).

```json
{
  "sub": "user_12345",
  "name": "Alice Dupont",
  "roles": ["ADMIN", "USER"],
  "iat": 1718476800,
  "exp": 1718480400
}
```

### 3.3 La Signature

La signature est générée mathématiquement par le serveur. Elle permet de **vérifier que le token n'a pas été altéré** en cours de route. 

Pour la créer, le serveur prend le Header encodé et le Payload encodé, les joint avec un point, puis signe le tout avec un secret (ou une clé privée) en utilisant l'algorithme spécifié dans le Header.

```javascript
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  votre_cle_secrete_hyper_securisee
)
```

**Pourquoi est-ce crucial ?** Si un attaquant modifie son rôle dans le Payload décodé (ex: passe de "USER" à "ADMIN") puis le ré-encode, la signature d'origine ne correspondra plus aux nouvelles données. Le serveur recalculera la signature, constatera la différence, et rejettera le token de l'attaquant.

> [!CAUTION]
> Le JWT n'est pas chiffré (juste encodé). **Ne jamais y stocker de données sensibles** (mot de passe, numéro de carte, etc.).
> 
> **Différence cruciale : Encodé vs Chiffré**
> *   **Encodé (ex: Base64) :** Transforme le format des données pour faciliter leur transport. **N'importe qui** peut décoder le Payload d'un JWT (ex: sur jwt.io) et lire son contenu en clair. L'encodage ne protège *pas* la confidentialité.
> *   **Chiffré (Cryptage) :** Rend les données totalement illisibles sans la clé secrète. Le chiffrement garantit la confidentialité.

---

## 4. Composants d’Authentification sur Spring Boot

Pour implémenter ces concepts dans une application Java, le framework **Spring Security** est la norme. Voici l'architecture et les composants clés impliqués, notamment dans le contexte d'une API REST Stateless (avec JWT) :

| Composant | Rôle principal |
| :--- | :--- |
| **`SecurityFilterChain`** | La configuration centrale de la sécurité. Définit quelles routes sont publiques, lesquelles nécessitent une authentification, et configure la chaîne de filtres. |
| **`AuthenticationManager`** | Le chef d'orchestre. Il reçoit la requête d'authentification brute et délègue la vérification au bon `AuthenticationProvider`. |
| **`AuthenticationProvider`** | La logique de validation métier. Par exemple, le `DaoAuthenticationProvider` vérifie que le mot de passe fourni correspond au hash stocké en base de données. |
| **`UserDetailsService`** | Interface (à implémenter via `loadUserByUsername`). Son rôle exclusif est d'aller chercher les données de l'utilisateur (souvent en BDD) pour les fournir à Spring Security. |
| **`UserDetails`** | Représente l'utilisateur du point de vue de Spring Security (identifiant, mot de passe hashé, état du compte, et rôles via `GrantedAuthority`). |
| **`OncePerRequestFilter`** | Classe de base souvent utilisée pour créer un filtre JWT personnalisé (ex: `JwtAuthenticationFilter`). Ce filtre intercepte chaque requête REST pour extraire et valider le token. |
| **`SecurityContextHolder`** | Un conteneur (basé sur le `ThreadLocal`) qui stocke le `SecurityContext`. Une fois l'utilisateur authentifié (token valide), il y est placé, rendant son identité accessible n'importe où dans l'application. |

### 4.1 Séquence de connexion avec login, password et génération du token

Ce flux se produit lorsque l'utilisateur s'authentifie pour la première fois pour obtenir son token.

![Diagramme de séquence](https://mermaid.ink/img/eyJjb2RlIjogInNlcXVlbmNlRGlhZ3JhbVxuICAgIHBhcnRpY2lwYW50IEMgYXMgQ2xpZW50XG4gICAgcGFydGljaXBhbnQgU0ZDIGFzIFNlY3VyaXR5RmlsdGVyQ2hhaW5cbiAgICBwYXJ0aWNpcGFudCBDdHJsIGFzIEF1dGhDb250cm9sbGVyXG4gICAgcGFydGljaXBhbnQgQU0gYXMgQXV0aGVudGljYXRpb25NYW5hZ2VyXG4gICAgcGFydGljaXBhbnQgVURTIGFzIFVzZXJEZXRhaWxzU2VydmljZVxuICAgIHBhcnRpY2lwYW50IERCIGFzIEJhc2UgZGUgRG9ublx1MDBlOWVzXG4gICAgcGFydGljaXBhbnQgSnd0U3ZjIGFzIEp3dFNlcnZpY2VcblxuICAgIEMtPj5TRkM6IDEuIFBPU1QgL2xvZ2luIHt1c2VybmFtZSwgcGFzc3dvcmR9XG4gICAgTm90ZSBvdmVyIFNGQzogQXV0b3Jpc2UgbGEgcm91dGUgKHBlcm1pdEFsbClcbiAgICBTRkMtPj5DdHJsOiAyLiBUcmFuc21ldCBhdSBjb250clx1MDBmNGxldXJcbiAgICBDdHJsLT4-QU06IDMuIGF1dGhlbnRpY2F0ZShVc2VybmFtZVBhc3N3b3JkQXV0aFRva2VuKVxuICAgIEFNLT4-VURTOiA0LiBsb2FkVXNlckJ5VXNlcm5hbWUodXNlcm5hbWUpXG4gICAgVURTLT4-REI6IDUuIENoZXJjaGUgbCd1dGlsaXNhdGV1clxuICAgIERCLS0-PlVEUzogNi4gUmV0b3VybmUgbCd1dGlsaXNhdGV1ciAoYXZlYyBoYXNoKVxuICAgIFVEUy0tPj5BTTogNy4gVXNlckRldGFpbHNcbiAgICBOb3RlIG92ZXIgQU06IENvbXBhcmUgbGUgbW90IGRlIHBhc3NlIGZvdXJuaTxici8-YXZlYyBsZSBoYXNoIHZpYSBQYXNzd29yZEVuY29kZXJcbiAgICBBTS0tPj5DdHJsOiA4LiBBdXRoZW50aWZpY2F0aW9uIHJcdTAwZTl1c3NpZVxuICAgIEN0cmwtPj5Kd3RTdmM6IDkuIGdlbmVyYXRlVG9rZW4oVXNlckRldGFpbHMpXG4gICAgSnd0U3ZjLS0-PkN0cmw6IDEwLiBKV1Qgc2lnblx1MDBlOVxuICAgIEN0cmwtLT4-U0ZDOiAxMS4gUlx1MDBlOXBvbnNlIEhUVFBcbiAgICBTRkMtLT4-QzogMTIuIDIwMCBPSyArIHsgXCJ0b2tlblwiOiBcImV5SmhiRy4uLlwiIH0iLCAibWVybWFpZCI6ICJ7XCJ0aGVtZVwiOiBcImRlZmF1bHRcIiwgXCJiYWNrZ3JvdW5kQ29sb3JcIjogXCJ3aGl0ZVwifSJ9)

**Explication détaillée de l'authentification initiale :**
1.  **Réception (1) :** Le client envoie ses identifiants sur un endpoint public (ex: `/auth/login`).
2.  **Filtrage (2) :** Le `SecurityFilterChain` intercepte la requête. Comme la route est configurée en `permitAll()`, il laisse passer la requête sans exiger de token et la transmet au Contrôleur.
3.  **Délégation (3) :** Le contrôleur délègue la tâche lourde à l'`AuthenticationManager` de Spring Security.
4.  **Récupération (4-7) :** L'`AuthenticationManager` fait appel au `UserDetailsService` pour aller chercher les détails de l'utilisateur dans la base de données. L'utilisateur est renvoyé sous forme d'objet `UserDetails`, contenant notamment le mot de passe *haché*.
5.  **Vérification (8) :** L'`AuthenticationManager` utilise un `PasswordEncoder` pour hacher le mot de passe reçu du client et le comparer avec le hash de la base de données.
6.  **Génération (9-10) :** Si les mots de passe correspondent, le contrôleur demande au `JwtService` de générer un nouveau token signé (le JWT).
7.  **Retour (11-12) :** Le token est renvoyé au client via la chaîne de sécurité. Le client devra le stocker pour ses futures requêtes.

### 4.2 Séquence de requête avec le token JWT

Ce flux se produit pour **toutes les requêtes subséquentes** vers des ressources protégées.

![Diagramme de séquence](https://mermaid.ink/img/eyJjb2RlIjogInNlcXVlbmNlRGlhZ3JhbVxuICAgIHBhcnRpY2lwYW50IEMgYXMgQ2xpZW50XG4gICAgcGFydGljaXBhbnQgU0ZDIGFzIFNlY3VyaXR5RmlsdGVyQ2hhaW5cbiAgICBwYXJ0aWNpcGFudCBGaWx0ZXIgYXMgSnd0QXV0aGVudGljYXRpb25GaWx0ZXJcbiAgICBwYXJ0aWNpcGFudCBKd3RTdmMgYXMgSnd0U2VydmljZVxuICAgIHBhcnRpY2lwYW50IFVEUyBhcyBVc2VyRGV0YWlsc1NlcnZpY2VcbiAgICBwYXJ0aWNpcGFudCBDb250ZXh0IGFzIFNlY3VyaXR5Q29udGV4dFxuICAgIHBhcnRpY2lwYW50IEN0cmwgYXMgQVBJIENvbnRyb2xsZXJcblxuICAgIEMtPj5TRkM6IDEuIEdFVCAvYXBpL2RhdGEgKEhlYWRlciAtPiBBdXRob3JpemF0aW9uOiBCZWFyZXIgPHRva2VuPilcbiAgICBTRkMtPj5GaWx0ZXI6IDIuIEFwcGxpcXVlIGxlIGZpbHRyZSAoYWRkRmlsdGVyQmVmb3JlKVxuICAgIEZpbHRlci0-Pkp3dFN2YzogMy4gZXh0cmFjdFVzZXJuYW1lKHRva2VuKVxuICAgIEp3dFN2Yy0tPj5GaWx0ZXI6IDQuIFwidXNlcm5hbWVcIlxuICAgIEZpbHRlci0-PlVEUzogNS4gbG9hZFVzZXJCeVVzZXJuYW1lKFwidXNlcm5hbWVcIikgKE9wdGlvbm5lbCopXG4gICAgVURTLS0-PkZpbHRlcjogNi4gVXNlckRldGFpbHNcbiAgICBGaWx0ZXItPj5Kd3RTdmM6IDcuIGlzVG9rZW5WYWxpZCh0b2tlbiwgVXNlckRldGFpbHMpXG4gICAgSnd0U3ZjLS0-PkZpbHRlcjogOC4gVmFsaWRlIChTaWduYXR1cmUgT0ssIE5vbiBleHBpclx1MDBlOSlcbiAgICBGaWx0ZXItPj5Db250ZXh0OiA5LiBFbnJlZ2lzdHJlIGwnQXV0aGVudGlmaWNhdGlvblxuICAgIEZpbHRlci0-PlNGQzogMTAuIFJlZG9ubmUgbGEgbWFpbiBcdTAwZTAgbGEgY2hhXHUwMGVlbmUgKGRvRmlsdGVyKVxuICAgIFNGQy0-PkN0cmw6IDExLiBBdHRlaW50IGxhIHJlc3NvdXJjZSBwcm90XHUwMGU5Z1x1MDBlOWVcbiAgICBDdHJsLS0-PlNGQzogMTIuIFJldG91cm5lIGxlcyBkb25uXHUwMGU5ZXNcbiAgICBTRkMtLT4-QzogMTMuIDIwMCBPSyAoRG9ublx1MDBlOWVzIHByb3RcdTAwZTlnXHUwMGU5ZXMpXG4gICAgXG4gICAgTm90ZSBvdmVyIEZpbHRlciwgVURTOiAqTm90ZSA6IEludGVycm9nZXIgbGEgREIgaWNpIGNvbnRyZWRpdCBsZSBwdXIgXCJTdGF0ZWxlc3NcIiw8YnIvPm1haXMgYydlc3Qgc291dmVudCBmYWl0IGRhbnMgU3ByaW5nIHBvdXIgcG91dm9pcjxici8-clx1MDBlOXZvcXVlciBpbnN0YW50YW5cdTAwZTltZW50IHVuIGNvbXB0ZSBiYW5uaS4iLCAibWVybWFpZCI6ICJ7XCJ0aGVtZVwiOiBcImRlZmF1bHRcIiwgXCJiYWNrZ3JvdW5kQ29sb3JcIjogXCJ3aGl0ZVwifSJ9)

**Explication détaillée de l'accès aux ressources protégées :**
1.  **Réception (1) :** Le client tente d'accéder à une route sécurisée en joignant son token dans l'en-tête `Authorization`.
2.  **Interception (2) :** La requête entre dans le `SecurityFilterChain`. Notre `JwtAuthenticationFilter` (qui a été inséré dans cette chaîne) intercepte la requête avant d'arriver au contrôleur.
3.  **Extraction (3-4) :** Le filtre demande au `JwtService` d'extraire le *username* (le `sub`) du payload du token.
4.  **Chargement de l'utilisateur (5-6) :** *Étape optionnelle en pur Stateless.* Le filtre charge l'objet `UserDetails` depuis la base de données pour vérifier que le compte n'est pas suspendu.
5.  **Validation Cryptographique (7-8) :** Le `JwtService` vérifie la **signature cryptographique** du token pour s'assurer qu'il n'a pas été falsifié, ainsi que sa date d'expiration.
6.  **Mise en Contexte (9) :** Si valide, le filtre informe Spring Security que cet utilisateur est authentifié pour cette requête. Il place l'authentification dans le `SecurityContextHolder`.
7.  **Accès au Contrôleur (10-13) :** Le filtre utilise `chain.doFilter(...)` pour redonner la main au `SecurityFilterChain`. La requête, désormais considérée comme "sûre", traverse le reste de la chaîne, atteint le Contrôleur, et la réponse est retournée au client.

### 4.3 Implémentation du SecurityFilterChain

Le `SecurityFilterChain` est le cœur de la configuration dans les versions récentes de Spring Security. C'est ici que l'on déclare l'API comme étant *Stateless*, que l'on définit les routes publiques/privées, et que l'on insère notre filtre JWT personnalisé.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Désactivation de la protection CSRF (inutile en API Stateless)
            .csrf(csrf -> csrf.disable())
            
            // 2. Configuration des règles d'autorisation des routes
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Routes publiques (login, inscription)
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Restreint aux administrateurs
                .anyRequest().authenticated() // Toutes les autres routes nécessitent un token valide
            )
            
            // 3. Configuration de la gestion de session en mode STATELESS
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 4. Déclaration du fournisseur d'authentification (inclut UserDetailsService et PasswordEncoder)
            .authenticationProvider(authenticationProvider)
            
            // 5. Ajout du filtre JWT avant le filtre standard d'authentification de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

**Points clés de cette configuration :**
*   **CSRF désactivé :** Les attaques CSRF exploitent généralement les cookies de session stockés par le navigateur. En mode purement *Stateless* avec des tokens passés dans l'en-tête `Authorization`, la protection CSRF de Spring n'est pas nécessaire.
*   **Session STATELESS :** Indique explicitement à Spring Security de ne jamais créer ni utiliser de session (`HttpSession`). Chaque requête doit être authentifiée de zéro via le JWT.
*   **`addFilterBefore` :** L'ordre des filtres est vital. On place notre `JwtAuthenticationFilter` *avant* le filtre classique `UsernamePasswordAuthenticationFilter` pour s'assurer que le JWT est intercepté et validé le plus tôt possible dans la chaîne.

---

## 5. Sécurité du Transport (HTTPS)

> [!IMPORTANT]
> Toutes les communications de l'API doivent être chiffrées en utilisant TLS (Transport Layer Security).

*   **Exiger HTTPS :** N'acceptez jamais de connexions sur HTTP (port 80). Redirigez toujours HTTP vers HTTPS ou rejetez la requête.
*   **Certificats Valides :** Utilisez des certificats TLS valides émis par une autorité de certification (CA) reconnue.
*   **HSTS (HTTP Strict Transport Security) :** Implémentez l'en-tête `Strict-Transport-Security` pour forcer les clients à n'utiliser que des connexions HTTPS.

## 6. Bonnes Pratiques d'Authentification

L'authentification vérifie l'identité de l'utilisateur ou du service appelant l'API.

*   **Jetons d'Accès (Access Tokens) :** Privilégiez l'utilisation de jetons (comme les JWT) via l'en-tête `Authorization: Bearer <token>`.
*   **OAuth 2.0 et OpenID Connect :** Utilisez ces standards pour la délégation d'autorisation et l'authentification fédérée.
*   **Stockage Sécurisé :** Les jetons ne doivent jamais être stockés dans des endroits non sécurisés côté client (comme le LocalStorage). Préférez les cookies `HttpOnly` et `Secure`.
*   **Durée de Vie Courte :** Les jetons d'accès doivent avoir une courte durée de vie. Utilisez des jetons de rafraîchissement (Refresh Tokens) pour obtenir de nouveaux jetons d'accès.

## 7. Bonnes Pratiques d'Autorisation

L'autorisation détermine si un utilisateur authentifié a les droits nécessaires pour effectuer une action.

*   **Principe de Moindre Privilège :** N'accordez que les permissions strictement nécessaires à l'accomplissement d'une tâche.
*   **Contrôle d'Accès Basé sur les Rôles (RBAC) :** Attribuez des rôles (ex: admin, utilisateur) et vérifiez ces rôles au niveau de l'API.
*   **Contrôle d'Accès Basé sur les Attributs (ABAC) :** Pour une granularité plus fine, vérifiez la propriété des ressources (ex: "L'utilisateur A ne peut modifier que le profil de l'utilisateur A").
*   **Ne faites pas confiance au client :** Ne vous fiez jamais aux données envoyées par le client pour déterminer ses droits (ex: ne pas se fier à un paramètre `isAdmin=true` dans la requête).

## 8. Validation et Sanitisation des Entrées

> [!CAUTION]
> Considérez toute donnée provenant du client comme potentiellement malveillante.

*   **Validation Stricte :** Validez toutes les entrées (corps de la requête, paramètres de l'URL, en-têtes) par rapport à un schéma strict (ex: type de données, longueur, format).
*   **Sanitisation :** Nettoyez les données pour empêcher les attaques par injection (XSS, SQL Injection, NoSQL Injection). Utilisez des bibliothèques reconnues pour l'échappement des données.
*   **Type de Contenu :** Vérifiez l'en-tête `Content-Type` de la requête (généralement `application/json`) et rejetez les autres types si vous ne les supportez pas.

## 9. Limitation de Taux (Rate Limiting) et Throttling

Protégez votre API contre les abus, les attaques par force brute et les dénis de service (DDoS).

*   **Rate Limiting :** Limitez le nombre de requêtes qu'un client (identifié par adresse IP ou jeton API) peut effectuer dans une fenêtre de temps donnée.
*   **En-têtes Explicites :** Renvoyez des en-têtes HTTP (ex: `X-RateLimit-Limit`, `X-RateLimit-Remaining`) pour informer les clients de leur statut.
*   **Code de Statut 429 :** Renvoyez un code d'erreur `429 Too Many Requests` lorsque la limite est dépassée.

## 10. Gestion des Erreurs et Journalisation (Logging)

*   **Ne fuyez pas d'informations :** Les messages d'erreur ne doivent jamais révéler de détails internes du système (stack traces, requêtes de base de données, versions de serveurs).
*   **Messages Génériques :** Renvoyez des messages d'erreur standardisés et génériques à l'utilisateur (ex: "Identifiants invalides" plutôt que "Mot de passe incorrect pour l'utilisateur X").
*   **Journalisation Sécurisée :** Logguez toutes les tentatives de connexion échouées, les accès refusés et les erreurs de validation des entrées.
*   **Ne pas logguer d'informations sensibles :** Assurez-vous de ne jamais écrire de mots de passe, de jetons d'accès ou de données personnelles sensibles dans vos journaux.

## 11. En-têtes de Sécurité HTTP

Utilisez les en-têtes HTTP pour renforcer la sécurité du navigateur client.

*   **CORS (Cross-Origin Resource Sharing) :** Configurez correctement les politiques CORS. N'utilisez pas le joker `*` si votre API nécessite une authentification. Spécifiez explicitement les origines autorisées.
*   **Content-Security-Policy (CSP) :** Empêche l'exécution de scripts non autorisés (XSS).
*   **X-Content-Type-Options: nosniff :** Empêche le navigateur de deviner le type MIME.
*   **X-Frame-Options: DENY ou SAMEORIGIN :** Protège contre le Clickjacking.

## 12. Méthodes HTTP et Architecture REST

*   **Idempotence :** Assurez-vous que les méthodes `PUT`, `DELETE` et `GET` sont idempotentes (plusieurs appels identiques ont le même effet qu'un seul).
*   **GET pour la Lecture :** N'utilisez jamais `GET` pour des opérations modifiant l'état du système (ex: `GET /deleteUser?id=123` est une faille de sécurité majeure).

## Liste de Contrôle Rapide

- [ ] L'API est uniquement accessible via HTTPS.
- [ ] L'authentification par JWT ou OAuth 2.0 est implémentée.
- [ ] Les autorisations sont vérifiées à chaque point de terminaison.
- [ ] Toutes les entrées utilisateurs sont validées et nettoyées.
- [ ] Des limites de taux (Rate Limiting) sont en place.
- [ ] Les messages d'erreur ne révèlent aucune information interne.
- [ ] Les en-têtes de sécurité (CORS, HSTS, etc.) sont configurés.
- [ ] Les mots de passe sont hachés (ex: avec bcrypt, Argon2) avant stockage.
