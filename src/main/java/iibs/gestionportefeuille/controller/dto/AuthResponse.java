package iibs.gestionportefeuille.controller.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String type,
        Long id,
        String nom,
        String email
) {}