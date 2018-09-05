package com.codesnippler.Validators;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { UserValidator.class })
public @interface ValidUser {
    String message() default "Invalid user";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String type() default "id";
}
