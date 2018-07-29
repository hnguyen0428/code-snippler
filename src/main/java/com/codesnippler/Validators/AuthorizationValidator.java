package com.codesnippler.Validators;


import com.codesnippler.Model.User;
import com.codesnippler.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuthorizationValidator implements ConstraintValidator<Authorized, HttpServletRequest> {

    private final UserRepository userRepo;
    private boolean required;

    @Autowired
    public AuthorizationValidator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public void initialize(Authorized constraintAnnotation) {
        required = constraintAnnotation.required();
    }


    @Override
    public boolean isValid(HttpServletRequest value, ConstraintValidatorContext context) {
        String apiKey = value.getHeader("Authorization");
        if (!required && apiKey == null) {
            return true;
        }

        User user = this.userRepo.findByApiKey(apiKey);

        if (user == null) {
            return !required;
        }
        else {
            // Inject user object into the request
            value.setAttribute("authorizedUser", user);
            return true;
        }
    }
}
