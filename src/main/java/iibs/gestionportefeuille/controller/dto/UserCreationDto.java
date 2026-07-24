package iibs.gestionportefeuille.controller.dto;

import iibs.gestionportefeuille.validation.EmailUnique;
import jakarta.validation.constraints.*;

public record UserCreationDto(

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 120, message = "Le nom doit contenir entre 2 et 120 caractères")
    String nom,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 150, message = "L'email ne peut dépasser 150 caractères")
    @EmailUnique
    String email

) {}