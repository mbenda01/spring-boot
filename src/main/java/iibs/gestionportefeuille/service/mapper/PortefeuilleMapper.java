package iibs.gestionportefeuille.service.mapper;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.*;
import iibs.gestionportefeuille.entity.enums.Devise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface PortefeuilleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "solde", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "devise", source = "dto.devise")
    @Mapping(target = "utilisateur", source = "utilisateur")
    Portefeuille versEntite(PortefeuilleCreationDto dto, Utilisateur utilisateur);

    @Mapping(target = "utilisateurId", source = "utilisateur.id")
    @Mapping(target = "nomUtilisateur", source = "utilisateur.nom")
    @Mapping(target = "libelleDevise", expression = "java(portefeuille.getDevise().getLibelle())")
    @Mapping(target = "soldeFormate",
             expression = "java(formater(portefeuille.getSolde(), portefeuille.getDevise()))")
    PortefeuilleResponseDto versReponse(Portefeuille portefeuille);

    default String formater(BigDecimal montant, Devise devise) {
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