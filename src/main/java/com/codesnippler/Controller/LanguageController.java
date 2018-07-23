package com.codesnippler.Controller;

import com.codesnippler.Model.Language;
import com.codesnippler.Repository.LanguageRepository;
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
@RequestMapping("/language")
public class LanguageController {

    private final LanguageRepository langRepo;

    @Autowired
    public LanguageController(LanguageRepository langRepo) {
        this.langRepo = langRepo;
    }


    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @PostMapping
    Language add(HttpServletRequest request) {
        String language = request.getParameter("name");
        String type = request.getParameter("type");
        Language newLang = this.langRepo.save(new Language(language, type, new Date()));
        return newLang;
    }

    @GetMapping("/all")
    List<Language> all(HttpServletRequest request) {
        return this.langRepo.findAll();
    }


}
