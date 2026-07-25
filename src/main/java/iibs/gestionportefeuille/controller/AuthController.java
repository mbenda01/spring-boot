package iibs.gestionportefeuille.controller;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Enregistrement et connexion (JWT)")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Créer un compte et recevoir un token")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> enregistrer(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.enregistrer(request));
    }

    @Operation(summary = "Se connecter et recevoir un token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> connecter(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.connecter(request));
    }
}
