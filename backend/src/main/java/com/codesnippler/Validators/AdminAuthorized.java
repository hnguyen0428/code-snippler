package com.codesnippler.Validators;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { AdminValidator.class })
public @interface AdminAuthorized {
    String message() default "Administrator Key is incorrect";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
