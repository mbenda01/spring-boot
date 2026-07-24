package iibs.gestionportefeuille.controller.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErreurResponse(
        LocalDateTime horodatage,
        int statut,
        String erreur,
        String message,
        Map<String, String> champs
) {}