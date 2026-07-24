package iibs.gestionportefeuille.controller.dto;

import iibs.gestionportefeuille.entity.enums.Devise;
import iibs.gestionportefeuille.validation.*;
import jakarta.validation.constraints.*;

@PortefeuilleUnique
public record PortefeuilleCreationDto(

    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    @Positive(message = "L'identifiant de l'utilisateur doit être strictement positif")
    @UtilisateurExiste
    Long utilisateurId,

    @NotNull(message = "La devise est obligatoire")
    Devise devise

) {}