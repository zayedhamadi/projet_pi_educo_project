package pi_project.Zayed.Validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Le numéro doit commencer par 5, 2 ou 9 et contenir 8 chiffres";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

