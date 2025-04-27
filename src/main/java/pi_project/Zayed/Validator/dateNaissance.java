package pi_project.Zayed.Validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = dateNaissanceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface dateNaissance {
    String message() default  "L'utilisateur doit avoir au moins 18 ans et la date ne peut pas être future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
