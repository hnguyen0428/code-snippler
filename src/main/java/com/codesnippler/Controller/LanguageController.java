package com.codesnippler.Controller;

import com.codesnippler.Model.Language;
import com.codesnippler.Repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/language")
public class LanguageController {

    private final LanguageRepository langRepo;

    @Autowired
    public LanguageController(LanguageRepository langRepo) {
        this.langRepo = langRepo;
    }


    @PostMapping
    Language create(@RequestParam(value = "name") String name,
                 @RequestParam(value = "type") String type) {
        return this.langRepo.save(new Language(name, type, new Date()));
    }

    @GetMapping("/all")
    List<Language> all(HttpServletRequest request) {
        return this.langRepo.findAll();
    }


}
