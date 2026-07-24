package iibs.gestionportefeuille.validation;

import jakarta.validation.*;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PortefeuilleUniqueValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PortefeuilleUnique {

    String message() default "Cet utilisateur possède déjà un portefeuille dans cette devise";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}