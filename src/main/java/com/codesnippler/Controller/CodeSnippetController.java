package com.codesnippler.Controller;

import com.codesnippler.Model.Language;
import com.codesnippler.Repository.CodeSnippetRepository;
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
@RequestMapping("/api/snippet")
public class CodeSnippetController {
    private final CodeSnippetRepository codeSnippetRepo;

    @Autowired
    public CodeSnippetController(CodeSnippetRepository codeSnippetRepo) {
        this.codeSnippetRepo = codeSnippetRepo;
    }
}
