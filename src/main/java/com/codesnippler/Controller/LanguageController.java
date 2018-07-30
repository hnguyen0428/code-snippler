package com.codesnippler.Controller;

import com.codesnippler.Model.Language;
import com.codesnippler.Repository.LanguageRepository;
import com.codesnippler.Utility.JsonUtility;
import com.codesnippler.Utility.ResponseBuilder;
import com.codesnippler.Validators.AdminAuthorized;
import com.codesnippler.Validators.ClientAuthorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/language")
@Validated
public class LanguageController {

    private final LanguageRepository langRepo;

    @Autowired
    public LanguageController(LanguageRepository langRepo) {
        this.langRepo = langRepo;
    }


    @PostMapping(produces = "application/json")
    @AdminAuthorized
    ResponseEntity create(@RequestParam(value = "name") String name,
                          @RequestParam(value = "type") String type) {
        Language language = this.langRepo.save(new Language(name, type, new Date()));
        String response = ResponseBuilder.createDataResponse(language.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = "application/json")
    ResponseEntity all(HttpServletRequest request) {
        List languages = this.langRepo.findAll();

        JsonArray json = JsonUtility.listToJson(languages);
        String response = ResponseBuilder.createDataResponse(json).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
