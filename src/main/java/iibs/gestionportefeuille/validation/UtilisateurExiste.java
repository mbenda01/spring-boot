package iibs.gestionportefeuille.validation;

import jakarta.validation.*;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UtilisateurExisteValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface UtilisateurExiste {

    String message() default "Aucun utilisateur ne correspond à cet identifiant";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}