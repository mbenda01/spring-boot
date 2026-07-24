package iibs.gestionportefeuille.controller;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.enums.Devise;
import iibs.gestionportefeuille.service.PortefeuilleService;
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
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Validated
@Tag(name = "Portefeuilles", description = "Gestion des portefeuilles")
public class PortefeuilleController {

    private final PortefeuilleService portefeuilleService;

    @Operation(summary = "Créer un portefeuille (solde initialisé à 0)")
    @PostMapping
    public ResponseEntity<PortefeuilleResponseDto> creer(
            @Valid @RequestBody PortefeuilleCreationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portefeuilleService.creer(dto));
    }

    @Operation(summary = "Consulter un portefeuille par son identifiant")
    @GetMapping("/{id}")
    public ResponseEntity<PortefeuilleResponseDto> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(portefeuilleService.trouverParId(id));
    }

    @Operation(summary = "Lister les portefeuilles avec pagination et filtres")
    @GetMapping
    public ResponseEntity<List<PortefeuilleResponseDto>> lister(
            @RequestParam(required = false) Long utilisateurId,
            @RequestParam(required = false) Devise devise,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Le numéro de page ne peut être négatif")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "La taille de page doit valoir au moins 1")
            @Max(value = 100, message = "La taille de page ne peut dépasser 100")
            int size) {

        return ResponseEntity.ok(portefeuilleService.lister(utilisateurId, devise, page, size));
    }
}