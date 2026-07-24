package iibs.gestionportefeuille.validation;

import iibs.gestionportefeuille.repository.UtilisateurRepository;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, String> {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return !utilisateurRepository.existsByEmail(email.trim().toLowerCase());
    }
}