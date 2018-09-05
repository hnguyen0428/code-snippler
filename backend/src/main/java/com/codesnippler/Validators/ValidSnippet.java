package com.codesnippler.Validators;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { SnippetValidator.class })
public @interface ValidSnippet  {
    String message() default "Snippet ID is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
