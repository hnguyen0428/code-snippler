package com.codesnippler.Validators;


import com.codesnippler.Model.Comment;
import com.codesnippler.Repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;


public class CommentValidator implements ConstraintValidator<ValidComment, String> {
    private final CommentRepository commentRepo;

    @Autowired
    public CommentValidator(CommentRepository commentRepo) {
        this.commentRepo = commentRepo;
    }

    @Override
    public void initialize(ValidComment constraintAnnotation) {}


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Optional<Comment> commentOpt = this.commentRepo.findById(value);

        if (!commentOpt.isPresent()) {
            return false;
        }
        else {
            HttpServletRequest request =
                    ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

            request.setAttribute("validComment", commentOpt.get());
            return true;
        }
    }
}
