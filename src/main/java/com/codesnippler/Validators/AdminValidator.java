package com.codesnippler.Validators;


import com.codesnippler.Utility.RandomKeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class AdminValidator implements ConstraintValidator<AdminAuthorized, Object> {
    @Value("${adminKey}")
    private String adminKey;


    @Override
    public void initialize(AdminAuthorized constraintAnnotation) {}


    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String key = request.getHeader("Admin-Key");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return key != null && encoder.matches(key, adminKey);
    }
}
