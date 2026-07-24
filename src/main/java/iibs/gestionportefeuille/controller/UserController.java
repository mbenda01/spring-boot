package iibs.gestionportefeuille.controller;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Créer un utilisateur")
    @PostMapping
    public ResponseEntity<UserResponseDto> creer(@Valid @RequestBody UserCreationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.creer(dto));
    }

    @Operation(summary = "Récupérer un utilisateur par son identifiant")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.trouverParId(id));
    }

    @Operation(summary = "Lister les utilisateurs avec pagination et filtres")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> lister(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String email,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Le numéro de page ne peut être négatif")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "La taille de page doit valoir au moins 1")
            @Max(value = 100, message = "La taille de page ne peut dépasser 100")
            int size) {

        return ResponseEntity.ok(userService.lister(nom, email, page, size));
    }
}