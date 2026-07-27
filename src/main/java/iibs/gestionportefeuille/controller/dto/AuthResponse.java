package iibs.gestionportefeuille.controller.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String type,
        Long id,
        String nom,
        String email
) {}