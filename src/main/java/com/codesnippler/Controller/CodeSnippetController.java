package com.codesnippler.Controller;

import com.codesnippler.Model.CodeSnippet;
import com.codesnippler.Model.Language;
import com.codesnippler.Model.User;
import com.codesnippler.Repository.CodeSnippetRepository;
import com.codesnippler.Repository.LanguageRepository;
import com.codesnippler.Repository.UserRepository;
import com.codesnippler.Exceptions.ErrorTypes;
import com.codesnippler.Utility.ResponseBuilder;
import com.codesnippler.Validators.Authorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/snippet")
@Validated
public class CodeSnippetController {
    private final CodeSnippetRepository codeSnippetRepo;
    private final UserRepository userRepo;
    private final LanguageRepository langRepo;

    @Autowired
    public CodeSnippetController(CodeSnippetRepository codeSnippetRepo, UserRepository userRepo,
                                 LanguageRepository langRepo) {
        this.codeSnippetRepo = codeSnippetRepo;
        this.userRepo = userRepo;
        this.langRepo = langRepo;
    }

    @PostMapping(produces = "application/json")
    String create(@Authorized HttpServletRequest request,
                  @RequestParam(value = "title") String title,
                  @RequestParam(value = "description", required = false) String description,
                  @RequestParam(value = "code") String code,
                  @RequestParam(value = "language") String languageName) {
        User user = (User)request.getAttribute("authorizedUser");

        Language language = this.langRepo.findByName(languageName);
        if (language == null) {
            JsonObject error = ResponseBuilder.createErrorObject("Invalid Language", ErrorTypes.INV_PARAM_ERROR);
            return ResponseBuilder.createErrorResponse(error).toString();
        }

        CodeSnippet snippet = new CodeSnippet(title, description, code, user.getId(), language.getId(), new Date());
        snippet = this.codeSnippetRepo.save(snippet);
        user.addToCreatedSnippets(snippet.getId());
        this.userRepo.save(user);

        return ResponseBuilder.createDataResponse(snippet.toJson()).toString();
    }


    @GetMapping(value = "/{snippetId}", produces = "application/json")
    String getSnippet(@PathVariable(value = "snippetId") String snippetId,
                      @RequestParam(value = "increaseViewcount", required = false) boolean shouldIncrease) {
        Optional<CodeSnippet> snippetOpt = this.codeSnippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            JsonObject error = ResponseBuilder.createErrorObject("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR);
            return ResponseBuilder.createErrorResponse(error).toString();
        }

        CodeSnippet snippet = snippetOpt.get();

        if (shouldIncrease) {
            snippet.setViewsCount(snippet.getViewsCount() + 1);
            snippet = this.codeSnippetRepo.save(snippet);
        }

        // Declare add on key value pairs to the JSON response
        Map<String, String> addOns = new HashMap<>();
        Optional<Language> language = this.langRepo.findById(snippet.getLanguageId());
        if (language.isPresent()) {
            addOns.put("language", language.get().getName());
        }

        JsonObject snippetJson = snippet.toJson(addOns);

        return ResponseBuilder.createDataResponse(snippetJson).toString();
    }
}
