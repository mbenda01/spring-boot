package iibs.gestionportefeuille.controller;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.enums.Devise;
import iibs.gestionportefeuille.service.PortefeuilleService;
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
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
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
    public ResponseEntity<Page<PortefeuilleResponseDto>> lister(
            @Parameter(description = "Filtre sur l'identifiant de l'utilisateur") @RequestParam(required = false) Long utilisateurId,
            @Parameter(description = "Filtre sur la devise") @RequestParam(required = false) Devise devise,
            @PageableDefault(size = 10, sort = "dateCreation", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(portefeuilleService.lister(utilisateurId, devise, pageable));
    }
}