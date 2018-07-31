package com.codesnippler.Controller;

import com.codesnippler.Model.CodeSnippet;
import com.codesnippler.Model.User;
import com.codesnippler.Repository.CodeSnippetRepository;
import com.codesnippler.Repository.UserRepository;
import com.codesnippler.Exceptions.ErrorTypes;
import com.codesnippler.Utility.JsonUtility;
import com.codesnippler.Utility.ResponseBuilder;
import com.codesnippler.Utility.RandomKeyGenerator;
import com.codesnippler.Validators.Authorized;
import com.codesnippler.Validators.ValidUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;


@RestController
@Validated
@RequestMapping("/api/user")
public class UserController {
    public static final int API_KEY_LENGTH = 128;

    private final UserRepository userRepo;
    private final CodeSnippetRepository snippetRepo;

    @Autowired
    public UserController(UserRepository userRepo, CodeSnippetRepository snippetRepo) {
        this.userRepo = userRepo;
        this.snippetRepo = snippetRepo;
    }


    @PostMapping(value = "/register", produces = "application/json")
    ResponseEntity register(@RequestParam(value = "username") @Size(min=6, max=20)
                            @Pattern(regexp = "^[a-zA-Z0-9_]*$") String username,
                            @RequestParam(value = "password") @Size(min=6, max=20)
                            @Pattern(regexp = "^[a-zA-Z0-9]*$") String password) {
        User user = this.userRepo.findByUsername(username);
        if (user != null) {
            String response = ResponseBuilder.createErrorResponse("Username already exists", ErrorTypes.INV_USERNAME_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPw = encoder.encode(password);

        RandomKeyGenerator keyGen = new RandomKeyGenerator(userRepo);
        String apiKey = keyGen.generateApiKey(API_KEY_LENGTH);

        User newUser = this.userRepo.save(new User(username, hashedPw, apiKey, new Date()));
        JsonObject userJson = newUser.toJson();

        String response = ResponseBuilder.createDataResponse(userJson).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/login", produces = "application/json")
    ResponseEntity login(HttpServletRequest request,
                         @RequestParam(value = "username") @ValidUser(type = "username") String username,
                         @RequestParam(value = "password") String password) {
        User user = (User)request.getAttribute("validUser");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (encoder.matches(password, user.getPassword())) {
            JsonObject userJson = user.toJson();
            String response = ResponseBuilder.createDataResponse(userJson).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            String response = ResponseBuilder.createErrorResponse("Password is incorrect", ErrorTypes.INV_AUTH_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/me", produces = "application/json")
    ResponseEntity getMyProfile(HttpServletRequest request,
                                @Authorized User user,
                                @RequestParam(value = "showSnippetDetails", required = false,
                                        defaultValue = "false") boolean showSnippetDetails) {
        String response;

        if (!showSnippetDetails) {
            response = ResponseBuilder.createDataResponse(user.toJson()).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Set<String> createdSnippetIds = user.getCreatedSnippets().keySet();
        Set<String> savedSnippetIds = user.getSavedSnippets().keySet();

        Iterable<CodeSnippet> createdSnippets = this.snippetRepo.findAllById(createdSnippetIds);
        Iterable<CodeSnippet> savedSnippets = this.snippetRepo.findAllById(savedSnippetIds);

        JsonObjectBuilder json = user.toJsonBuilder();
        json.add("createdSnippets", JsonUtility.listToJson(createdSnippets));
        json.add("savedSnippets", JsonUtility.listToJson(savedSnippets));

        response = ResponseBuilder.createDataResponse(json.build()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/{userId}", produces = "application/json")
    ResponseEntity getUser(HttpServletRequest request,
                           @RequestParam(value = "showSnippetDetails", required = false,
                                   defaultValue = "false") boolean showSnippetDetails,
                           @PathVariable(value = "userId") @ValidUser String userId) {
        User user = (User)request.getAttribute("validUser");
        String response;

        if (!showSnippetDetails) {
            response = ResponseBuilder.createDataResponse(user.toJson()).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Set<String> createdSnippetIds = user.getCreatedSnippets().keySet();
        Set<String> savedSnippetIds = user.getSavedSnippets().keySet();

        Iterable<CodeSnippet> createdSnippets = this.snippetRepo.findAllById(createdSnippetIds);
        Iterable<CodeSnippet> savedSnippets = this.snippetRepo.findAllById(savedSnippetIds);

        JsonObjectBuilder json = user.toJsonBuilder();
        json.add("createdSnippets", JsonUtility.listToJson(createdSnippets));
        json.add("savedSnippets", JsonUtility.listToJson(savedSnippets));

        response = ResponseBuilder.createDataResponse(json.build()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/savedSnippets", produces = "application/json")
    ResponseEntity getSavedSnippets(HttpServletRequest request,
                                    @Authorized User user,
                                    @RequestParam(value = "showDetails", required = false) boolean showDetails) {
        Map<String, Boolean> snippetIdsMap = user.getSavedSnippets();
        Set snippetIds = snippetIdsMap.keySet();

        JsonArray snippetsArray = JsonUtility.listToJson(snippetIds);
        if (!showDetails) {
            String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Iterable itr = this.snippetRepo.findAllById(snippetIds);
        snippetsArray = JsonUtility.listToJson(itr);
        String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/{userId}/savedSnippets", produces = "application/json")
    ResponseEntity getSavedSnippets(HttpServletRequest request,
                                    @Authorized User authorizedUser,
                                    @PathVariable(value = "userId") @ValidUser String userId,
                                    @RequestParam(value = "showDetails", required = false) boolean showDetails) {
        User user = (User)request.getAttribute("validUser");
        Map<String, Boolean> snippetIdsMap = user.getSavedSnippets();
        Set<String> snippetIds = snippetIdsMap.keySet();

        JsonArray snippetsArray = JsonUtility.listToJson(snippetIds);
        if (!showDetails) {
            String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Iterable itr = this.snippetRepo.findAllById(snippetIds);
        snippetsArray = JsonUtility.listToJson(itr);
        String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/createdSnippets", produces = "application/json")
    ResponseEntity getCreatedSnippets(HttpServletRequest request,
                                      @Authorized User user,
                                      @RequestParam(value = "showDetails", required = false) boolean showDetails) {
        Map<String, Boolean> snippetIdsMap = user.getCreatedSnippets();
        Set snippetIds = snippetIdsMap.keySet();

        JsonArray snippetsArray = JsonUtility.listToJson(snippetIds);
        if (!showDetails) {
            String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Iterable itr = this.snippetRepo.findAllById(snippetIds);
        snippetsArray = JsonUtility.listToJson(itr);
        String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/{userId}/createdSnippets", produces = "application/json")
    ResponseEntity getCreatedSnippets(HttpServletRequest request,
                                      @Authorized User authorizedUser,
                                      @PathVariable(value = "userId") @ValidUser String userId,
                                      @RequestParam(value = "showDetails", required = false) boolean showDetails) {
        User user = (User)request.getAttribute("validUser");
        Map<String, Boolean> snippetIdsMap = user.getCreatedSnippets();
        Set snippetIds = snippetIdsMap.keySet();

        JsonArray snippetsArray = JsonUtility.listToJson(snippetIds);
        if (!showDetails) {
            String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Iterable itr = this.snippetRepo.findAllById(snippetIds);
        snippetsArray = JsonUtility.listToJson(itr);
        String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
