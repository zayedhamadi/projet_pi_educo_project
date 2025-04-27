package pi_project.Zayed.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class dateNaissanceValidator implements ConstraintValidator<dateNaissance, LocalDate> {
    @Override
    public boolean isValid(LocalDate dateNaissance, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = true;
        LocalDate today = LocalDate.now();
        int age = Period.between(dateNaissance, today).getYears();
        if (dateNaissance.isAfter(today) || age < 18)
            isValid = false;

        return isValid;
    }
}
