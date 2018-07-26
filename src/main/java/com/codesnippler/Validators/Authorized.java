package com.codesnippler.Validators;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { AuthorizationValidator.class })
public @interface Authorized {
    String message() default "User is not authorized";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
