package pi_project.Zayed.Validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, Integer> {

    @Override
    public boolean isValid(Integer numTel, ConstraintValidatorContext context) {
        if (numTel == null) return false;

        String numStr = String.valueOf(numTel);
        return numStr.length() == 8 && "529".contains(numStr.substring(0, 1));
    }
}
