package com.codesnippler.Validators;


import com.codesnippler.Utility.RandomKeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ClientValidator implements ConstraintValidator<ClientAuthorized, Object> {
    @Value("${clientKey}")
    private String clientKey;


    @Override
    public void initialize(ClientAuthorized constraintAnnotation) {}


    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String key = request.getHeader("clientKey");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return key != null && encoder.matches(key, clientKey);
    }
}
