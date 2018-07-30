package com.codesnippler.Validators;

import com.codesnippler.Model.Language;
import com.codesnippler.Repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LanguageNameValidator implements ConstraintValidator<ValidLanguageName, String> {

    private final LanguageRepository langRepo;


    @Autowired
    public LanguageNameValidator(LanguageRepository langRepo) {
        this.langRepo = langRepo;
    }


    @Override
    public void initialize(ValidLanguageName constraintAnnotation) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        HttpServletRequest request =
                ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

        Language language = this.langRepo.findByName(value);
        if (language == null) {
            return false;
        }
        else {
            request.setAttribute("validLanguage", language);
            return true;
        }
    }
}
