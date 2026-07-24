package iibs.gestionportefeuille.validation;

import iibs.gestionportefeuille.controller.dto.PortefeuilleCreationDto;
import iibs.gestionportefeuille.repository.PortefeuilleRepository;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortefeuilleUniqueValidator
        implements ConstraintValidator<PortefeuilleUnique, PortefeuilleCreationDto> {

    private final PortefeuilleRepository portefeuilleRepository;

    @Override
    public boolean isValid(PortefeuilleCreationDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.utilisateurId() == null || dto.devise() == null) {
            return true;
        }
        return !portefeuilleRepository.existsByUtilisateurIdAndDevise(
                dto.utilisateurId(), dto.devise());
    }
}