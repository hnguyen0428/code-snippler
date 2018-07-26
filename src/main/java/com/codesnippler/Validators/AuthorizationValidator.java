package com.codesnippler.Validators;


import com.codesnippler.Model.User;
import com.codesnippler.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuthorizationValidator implements ConstraintValidator<Authorized, HttpServletRequest> {

    private final UserRepository userRepo;

    @Autowired
    public AuthorizationValidator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public void initialize(Authorized constraintAnnotation) {}


    @Override
    public boolean isValid(HttpServletRequest value, ConstraintValidatorContext context) {
        String apiKey = value.getHeader("Authorization");

        User user = this.userRepo.findByApiKey(apiKey);

        if (user == null) {
            return false;
        }
        else {
            // Inject user object into the request
            value.setAttribute("authorizedUser", user);
            return true;
        }
    }
}
