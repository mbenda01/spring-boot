package iibs.gestionportefeuille.controller.dto;

import iibs.gestionportefeuille.validation.EmailUnique;
import jakarta.validation.constraints.*;

public record RegisterRequest(

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 120, message = "Le nom doit contenir entre 2 et 120 caractères")
    String nom,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @EmailUnique
    String email,

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    String motDePasse

) {}