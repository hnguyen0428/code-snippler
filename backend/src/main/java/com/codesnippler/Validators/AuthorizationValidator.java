package com.codesnippler.Validators;


import com.codesnippler.Model.User;
import com.codesnippler.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuthorizationValidator implements ConstraintValidator<Authorized, User> {

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
    public boolean isValid(User value, ConstraintValidatorContext context) {
        HttpServletRequest request =
                ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String apiKey = request.getHeader("Authorization");
        if (!required && apiKey == null) {
            return true;
        }

        User user = this.userRepo.findByApiKey(apiKey);

        if (user == null) {
            return false;
        }
        else {
            user.copyTo(value);
            value.setAuthenticated();
            return true;
        }
    }
}
