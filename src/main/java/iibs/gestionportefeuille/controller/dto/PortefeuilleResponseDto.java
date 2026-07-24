package iibs.gestionportefeuille.controller.dto;

import iibs.gestionportefeuille.entity.enums.Devise;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PortefeuilleResponseDto(
        Long id,
        Long utilisateurId,
        String nomUtilisateur,
        BigDecimal solde,
        String soldeFormate,
        Devise devise,
        String libelleDevise,
        LocalDateTime dateCreation
) {}