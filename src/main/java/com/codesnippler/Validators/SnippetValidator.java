package com.codesnippler.Validators;

import com.codesnippler.Model.CodeSnippet;
import com.codesnippler.Repository.CodeSnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;


public class SnippetValidator implements ConstraintValidator<ValidSnippet, CodeSnippet> {
    private final CodeSnippetRepository snippetRepo;

    @Autowired
    public SnippetValidator(CodeSnippetRepository snippetRepo) {
        this.snippetRepo = snippetRepo;
    }

    @Override
    public void initialize(ValidSnippet constraintAnnotation) {}


    @Override
    public boolean isValid(CodeSnippet value, ConstraintValidatorContext context) {
        if (value == null)
            return false;

        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(value.getId());

        if (!snippetOpt.isPresent()) {
            return false;
        }
        else {
//            snippetOpt.get().copyTo(value);
            return true;
        }
    }
}
