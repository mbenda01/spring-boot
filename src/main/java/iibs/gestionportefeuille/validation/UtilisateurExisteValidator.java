package iibs.gestionportefeuille.validation;

import iibs.gestionportefeuille.repository.UtilisateurRepository;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UtilisateurExisteValidator implements ConstraintValidator<UtilisateurExiste, Long> {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public boolean isValid(Long utilisateurId, ConstraintValidatorContext context) {
        if (utilisateurId == null) {
            return true;
        }
        return utilisateurRepository.existsById(utilisateurId);
    }
}