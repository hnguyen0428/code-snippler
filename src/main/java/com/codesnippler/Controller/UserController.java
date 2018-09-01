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
import com.codesnippler.Validators.ClientAuthorized;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;


@RestController
@CrossOrigin
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
    @ClientAuthorized
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
        JsonObjectBuilder userJson = newUser.toJsonBuilder();
        userJson.add("apiKey", newUser.getApiKey());

        String response = ResponseBuilder.createDataResponse(userJson.build()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/login", produces = "application/json")
    @ClientAuthorized
    ResponseEntity login(HttpServletRequest request,
                         @RequestParam(value = "username") @ValidUser(type = "username") String username,
                         @RequestParam(value = "password") String password) {
        User user = (User)request.getAttribute("validUser");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (encoder.matches(password, user.getPassword())) {
            JsonObjectBuilder json = user.toJsonBuilder();
            json.add("apiKey", user.getApiKey());
            String response = ResponseBuilder.createDataResponse(json.build()).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            String response = ResponseBuilder.createErrorResponse("Password is incorrect", ErrorTypes.INV_AUTH_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(value = "/changePassword", produces = "application/json")
    @ClientAuthorized
    ResponseEntity changePassword(@Authorized User user,
                                  @RequestParam(value = "currentPassword") @Size(min=6, max=20)
                                  @Pattern(regexp = "^[a-zA-Z0-9]*$") String currPw,
                                  @RequestParam(value = "newPassword") @Size(min=6, max=20)
                                  @Pattern(regexp = "^[a-zA-Z0-9]*$") String newPw) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (encoder.matches(currPw, user.getPassword())) {
            String hashedPw = encoder.encode(newPw);
            user.setPassword(hashedPw);
            user = this.userRepo.save(user);
            String response = ResponseBuilder.createDataResponse(user.toJson()).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            String response = ResponseBuilder.createErrorResponse("Current Password is incorrect", ErrorTypes.INV_AUTH_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping(value = "/profile", produces = "application/json")
    @ClientAuthorized
    ResponseEntity updateProfile(@Authorized User user,
                                 @RequestParam(value = "firstName", required = false) @Size(max = 256) String firstName,
                                 @RequestParam(value = "lastName", required = false) @Size(max = 256) String lastName,
                                 @RequestParam(value = "email", required = false) @Email String email) {
        if (firstName != null)
            user.updateProfile("firstName", firstName);
        if (lastName != null)
            user.updateProfile("lastName", lastName);
        if (email != null)
            user.updateProfile("email", email);

        this.userRepo.save(user);
        String response = ResponseBuilder.createDataResponse(user.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private ResponseEntity getUserProfile(User user, boolean showSnippetDetails) {
        if (showSnippetDetails) {
            user.includeCreatedSnippetsDetails(snippetRepo);
            user.includeSavedSnippetsDetails(snippetRepo);
        }

        String response = ResponseBuilder.createDataResponse(user.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/me", produces = "application/json")
    ResponseEntity getMyProfile(@Authorized User user,
                                @RequestParam(value = "showSnippetDetails", required = false,
                                        defaultValue = "false") boolean showSnippetDetails) {
        return this.getUserProfile(user, showSnippetDetails);
    }


    @GetMapping(value = "/{userId}", produces = "application/json")
    ResponseEntity getUser(@RequestParam(value = "showSnippetDetails", required = false,
                                   defaultValue = "false") boolean showSnippetDetails,
                           @PathVariable(value = "userId") @NotNull(message = "Invalid User ID") User user) {
        return this.getUserProfile(user, showSnippetDetails);
    }


    @GetMapping(value = "/byIds", produces = "application/json")
    ResponseEntity getUsers(@RequestParam(value = "ids") List<String> userIds) {
        Iterable<User> users = this.userRepo.findAllById(userIds);
        JsonArray usersJson = JsonUtility.listToJson(users);
        String response = ResponseBuilder.createDataResponse(usersJson).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private ResponseEntity getUserSavedSnippets(User user) {
        Map<String, Boolean> snippetIdsMap = user.getSavedSnippets();
        Set<String> snippetIds = snippetIdsMap.keySet();

        Iterable itr = this.snippetRepo.findAllById(snippetIds);
        JsonArray snippetsArray = JsonUtility.listToJson(itr);
        String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/savedSnippets", produces = "application/json")
    ResponseEntity getSavedSnippets(@Authorized User user) {
        return this.getUserSavedSnippets(user);
    }


    @GetMapping(value = "/{userId}/savedSnippets", produces = "application/json")
    ResponseEntity getSavedSnippets(@Authorized User authorizedUser,
                                    @PathVariable(value = "userId") @NotNull(message = "Invalid User ID") User user) {
        return this.getUserSavedSnippets(user);
    }


    private ResponseEntity getUserCreatedSnippets(User user) {
        Map<String, Boolean> snippetIdsMap = user.getCreatedSnippets();
        Set<String> snippetIds = snippetIdsMap.keySet();

        Iterable itr = this.snippetRepo.findAllById(snippetIds);
        JsonArray snippetsArray = JsonUtility.listToJson(itr);
        String response = ResponseBuilder.createDataResponse(snippetsArray).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/createdSnippets", produces = "application/json")
    ResponseEntity getCreatedSnippets(@Authorized User user) {
        return this.getUserCreatedSnippets(user);
    }


    @GetMapping(value = "/{userId}/createdSnippets", produces = "application/json")
    ResponseEntity getCreatedSnippets(@Authorized User authorizedUser,
                                      @PathVariable(value = "userId") @NotNull(message = "Invalid User ID") User user) {
        return this.getUserCreatedSnippets(user);
    }
}
