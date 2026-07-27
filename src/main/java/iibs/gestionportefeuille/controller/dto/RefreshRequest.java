package iibs.gestionportefeuille.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(

    @NotBlank(message = "Le refresh token est obligatoire")
    String refreshToken

) {}