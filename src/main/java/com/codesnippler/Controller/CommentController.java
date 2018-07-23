package com.codesnippler.Controller;

import com.codesnippler.Model.Comment;
import com.codesnippler.Repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentRepository commentRepo;

    @Autowired
    public CommentController(CommentRepository commentRepo) {
        this.commentRepo = commentRepo;
    }
}
