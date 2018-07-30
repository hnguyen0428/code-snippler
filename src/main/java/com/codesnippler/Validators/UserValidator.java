package com.codesnippler.Validators;

import com.codesnippler.Model.User;
import com.codesnippler.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UserValidator implements ConstraintValidator<ValidUser, String> {

    private String type;
    private final UserRepository userRepo;


    @Autowired
    public UserValidator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public void initialize(ValidUser constraintAnnotation) {
        type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        User user = null;

        switch (type) {
            case "id":
                Optional<User> userOpt = this.userRepo.findById(value);
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                }
                break;
            case "username":
                user = this.userRepo.findByUsername(value);
                break;
        }

        if (user == null) {
            return false;
        }
        else {
            HttpServletRequest request =
                    ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            request.setAttribute("validUser", user);
            return true;
        }
    }
}
