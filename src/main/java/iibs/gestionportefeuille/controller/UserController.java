package iibs.gestionportefeuille.controller;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
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
    public ResponseEntity<Page<UserResponseDto>> lister(
            @Parameter(description = "Filtre partiel sur le nom") @RequestParam(required = false) String nom,
            @Parameter(description = "Filtre partiel sur l'email") @RequestParam(required = false) String email,
            @PageableDefault(size = 10, sort = "nom", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(userService.lister(nom, email, pageable));
    }
}