package com.codesnippler.Validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { LanguageNameValidator.class })
public @interface ValidLanguageName {
    String message() default "Unsupported language";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
