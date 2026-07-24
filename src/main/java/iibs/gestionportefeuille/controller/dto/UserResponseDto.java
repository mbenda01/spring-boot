package iibs.gestionportefeuille.controller.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponseDto(
        Long id,
        String nom,
        String email,
        LocalDateTime dateCreation
) {}