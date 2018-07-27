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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity create(@Authorized HttpServletRequest request,
                          @RequestParam(value = "title") String title,
                          @RequestParam(value = "description", required = false) String description,
                          @RequestParam(value = "code") String code,
                          @RequestParam(value = "language") String languageName) {
        User user = (User)request.getAttribute("authorizedUser");

        Language language = this.langRepo.findByName(languageName);
        if (language == null) {
            JsonObject error = ResponseBuilder.createErrorObject("Invalid Language", ErrorTypes.INV_PARAM_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        CodeSnippet snippet = new CodeSnippet(title, description, code, user.getId(), language.getId(), new Date());
        snippet = this.codeSnippetRepo.save(snippet);
        user.addToCreatedSnippets(snippet.getId());
        this.userRepo.save(user);

        String response = ResponseBuilder.createDataResponse(snippet.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/{snippetId}", produces = "application/json")
    ResponseEntity getSnippet(@PathVariable(value = "snippetId") String snippetId,
                              @RequestParam(value = "increaseViewcount", required = false) boolean shouldIncrease) {
        Optional<CodeSnippet> snippetOpt = this.codeSnippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            JsonObject error = ResponseBuilder.createErrorObject("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        CodeSnippet snippet = snippetOpt.get();

        if (shouldIncrease) {
            snippet.incrementViewsCount();
            snippet = this.codeSnippetRepo.save(snippet);
        }

        // Declare add on key value pairs to the JSON response
        Map<String, String> addOns = new HashMap<>();
        Optional<Language> language = this.langRepo.findById(snippet.getLanguageId());
        if (language.isPresent()) {
            addOns.put("language", language.get().getName());
        }

        JsonObject snippetJson = snippet.toJson(addOns);

        String response = ResponseBuilder.createDataResponse(snippetJson).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{snippetId}/upvote", produces = "application/json")
    ResponseEntity upvote(@Authorized HttpServletRequest request,
                          @PathVariable(value = "snippetId") String snippetId) {
        Optional<CodeSnippet> snippetOpt = this.codeSnippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            JsonObject error = ResponseBuilder.createErrorObject("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = snippetOpt.get();
        HashMap<String, Boolean> upvoters = snippet.getUpvoters();

        if (!upvoters.containsKey(authorizedUser.getId())) {
            snippet.addToUpvoters(authorizedUser.getId());

            // Remove this user from downvoters if he downvoted the snippet before
            if (snippet.getDownvoters().get(authorizedUser.getId()) != null) {
                snippet.removeFromDownvoters(authorizedUser.getId());
            }

            this.codeSnippetRepo.save(snippet);
            String response = ResponseBuilder.createSuccessResponse().toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            JsonObject error = ResponseBuilder.createErrorObject("User has already upvoted the Snippet", ErrorTypes.INV_REQUEST_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping(value = "/{snippetId}/downvote", produces = "application/json")
    ResponseEntity downvote(@Authorized HttpServletRequest request,
                            @PathVariable(value = "snippetId") String snippetId) {
        Optional<CodeSnippet> snippetOpt = this.codeSnippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            JsonObject error = ResponseBuilder.createErrorObject("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = snippetOpt.get();
        HashMap<String, Boolean> downvoters = snippet.getDownvoters();

        if (!downvoters.containsKey(authorizedUser.getId())) {
            snippet.addToDownvoters(authorizedUser.getId());

            // Remove this user from upvoters if he upvoted the snippet before
            if (snippet.getUpvoters().get(authorizedUser.getId()) != null) {
                snippet.removeFromUpvoters(authorizedUser.getId());
            }

            this.codeSnippetRepo.save(snippet);
            String response = ResponseBuilder.createSuccessResponse().toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            JsonObject error = ResponseBuilder.createErrorObject("User has already downvoted the Snippet", ErrorTypes.INV_REQUEST_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping(value = "/{snippetId}/save", produces = "application/json")
    ResponseEntity saveSnippet(@Authorized HttpServletRequest request,
                               @PathVariable(value = "snippetId") String snippetId) {
        Optional<CodeSnippet> snippetOpt = this.codeSnippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            JsonObject error = ResponseBuilder.createErrorObject("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = snippetOpt.get();
        HashMap<String, Boolean> savedSnippets = authorizedUser.getSavedSnippets();

        if (!savedSnippets.containsKey(snippetId)) {
            authorizedUser.addToSavedSnippets(snippetId);
            snippet.incrementSavedCount();
            this.codeSnippetRepo.save(snippet);
            this.userRepo.save(authorizedUser);
            String response = ResponseBuilder.createSuccessResponse().toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            JsonObject error = ResponseBuilder.createErrorObject("User has already saved the Snippet", ErrorTypes.INV_REQUEST_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
