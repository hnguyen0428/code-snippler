package com.codesnippler.Validators;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { CommentValidator.class })
public @interface ValidComment  {
    String message() default "Comment ID is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
