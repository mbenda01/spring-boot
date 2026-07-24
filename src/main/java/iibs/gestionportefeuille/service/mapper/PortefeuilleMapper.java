package iibs.gestionportefeuille.service.mapper;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.*;
import iibs.gestionportefeuille.entity.enums.Devise;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.*;
import java.util.Locale;

@Component
public class PortefeuilleMapper {

    public Portefeuille versEntite(PortefeuilleCreationDto dto, Utilisateur utilisateur) {
        Portefeuille portefeuille = new Portefeuille();
        portefeuille.setUtilisateur(utilisateur);
        portefeuille.setDevise(dto.devise());
        portefeuille.setSolde(BigDecimal.ZERO);
        return portefeuille;
    }

    public PortefeuilleResponseDto versReponse(Portefeuille portefeuille) {
        return PortefeuilleResponseDto.builder()
                .id(portefeuille.getId())
                .utilisateurId(portefeuille.getUtilisateur().getId())
                .nomUtilisateur(portefeuille.getUtilisateur().getNom())
                .solde(portefeuille.getSolde())
                .soldeFormate(formater(portefeuille.getSolde(), portefeuille.getDevise()))
                .devise(portefeuille.getDevise())
                .libelleDevise(portefeuille.getDevise().getLibelle())
                .dateCreation(portefeuille.getDateCreation())
                .build();
    }

    private String formater(BigDecimal montant, Devise devise) {
        if (montant == null || devise == null) {
            return null;
        }

        DecimalFormatSymbols symboles = new DecimalFormatSymbols(Locale.FRANCE);
        symboles.setGroupingSeparator(' ');

        StringBuilder motif = new StringBuilder("#,##0");
        if (devise.getNombreDecimales() > 0) {
            motif.append('.').append("0".repeat(devise.getNombreDecimales()));
        }

        return new DecimalFormat(motif.toString(), symboles).format(montant)
                + " " + devise.name();
    }
}