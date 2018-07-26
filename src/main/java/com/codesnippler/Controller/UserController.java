package com.codesnippler.Controller;

import com.codesnippler.Model.User;
import com.codesnippler.Repository.UserRepository;
import com.codesnippler.Exceptions.ErrorTypes;
import com.codesnippler.Utility.ResponseBuilder;
import com.codesnippler.Utility.RandomKeyGenerator;
import com.codesnippler.Validators.Authorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Optional;


@RestController
@Validated
@RequestMapping("/api/user")
public class UserController {
    public static final int API_KEY_LENGTH = 64;

    private final UserRepository userRepo;

    @Autowired
    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping(value = "/register", produces = "application/json")
    ResponseEntity register(@RequestParam(value = "username") @Size(min=6, max=20)
                            @Pattern(regexp = "^[a-zA-Z0-9_]*$") String username,
                            @RequestParam(value = "password") @Size(min=6, max=20)
                            @Pattern(regexp = "^[a-zA-Z0-9]*$") String password) {
        User user = this.userRepo.findByUsername(username);
        if (user != null) {
            JsonObject error = ResponseBuilder.createErrorObject("Username already exists", ErrorTypes.INV_USERNAME_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPw = encoder.encode(password);

        String apiKey = RandomKeyGenerator.generateApiKey(API_KEY_LENGTH);

        User newUser = this.userRepo.save(new User(username, hashedPw, apiKey, new Date()));
        JsonObject userJson = newUser.toJson();

        String response = ResponseBuilder.createDataResponse(userJson).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/login", produces = "application/json")
    ResponseEntity login(@RequestParam(value = "username") String username,
                         @RequestParam(value = "password") String password) {
        User user = this.userRepo.findByUsername(username);
        if (user == null) {
            JsonObject error = ResponseBuilder.createErrorObject("Username cannot be found", ErrorTypes.INV_AUTH_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (encoder.matches(password, user.getPassword())) {
            JsonObject userJson = user.toJson();
            String response = ResponseBuilder.createDataResponse(userJson).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            JsonObject error = ResponseBuilder.createErrorObject("Password is incorrect", ErrorTypes.INV_AUTH_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{userId}", produces = "application/json")
    ResponseEntity getUser(@Authorized HttpServletRequest request,
                           @PathVariable(value = "userId") String userId) {
        Optional<User> user = this.userRepo.findById(userId);
        if (!user.isPresent()) {
            JsonObject error = ResponseBuilder.createErrorObject("User cannot be found", ErrorTypes.INV_PARAM_ERROR);
            String response = ResponseBuilder.createErrorResponse(error).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String response = ResponseBuilder.createDataResponse(user.get().toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
