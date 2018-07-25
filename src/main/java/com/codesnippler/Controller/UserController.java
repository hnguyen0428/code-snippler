package com.codesnippler.Controller;

import com.codesnippler.Model.User;
import com.codesnippler.Repository.UserRepository;
import com.codesnippler.Utility.ErrorTypes;
import com.codesnippler.Utility.ResponseBuilder;
import com.codesnippler.Utility.RandomKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;


@RestController
@RequestMapping("/api/user")
public class UserController {
    public static final int API_KEY_LENGTH = 64;

    private final UserRepository userRepo;

    @Autowired
    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping(value = "/register", produces = "application/json")
    String register(HttpServletRequest request) {
        String username = request.getParameter("username");

        User user = this.userRepo.findByUsername(username);
        if (user != null) {
            JsonObject error = ResponseBuilder.createErrorObject("Username already exists", ErrorTypes.INV_USERNAME_ERROR);
            return ResponseBuilder.createErrorResponse(error).toString();
        }

        String password = request.getParameter("password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPw = encoder.encode(password);

        String apiKey = RandomKeyGenerator.generateApiKey(API_KEY_LENGTH);

        User newUser = this.userRepo.save(new User(username, hashedPw, apiKey, new Date()));
        JsonObject userJson = newUser.toJson(Arrays.asList("password"));

        return ResponseBuilder.createDataResponse(userJson).toString();
    }

    @PostMapping(value = "/login", produces = "application/json")
    String login(HttpServletRequest request) {
        String username = request.getParameter("username");

        User user = this.userRepo.findByUsername(username);
        if (user == null) {
            JsonObject error = ResponseBuilder.createErrorObject("Username cannot be found", ErrorTypes.INV_AUTH_ERROR);
            return ResponseBuilder.createErrorResponse(error).toString();
        }

        String password = request.getParameter("password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (encoder.matches(password, user.getPassword())) {
            JsonObject userJson = user.toJson(Arrays.asList("password"));
            return ResponseBuilder.createDataResponse(userJson).toString();
        }
        else {
            JsonObject error = ResponseBuilder.createErrorObject("Password is incorrect", ErrorTypes.INV_AUTH_ERROR);
            return ResponseBuilder.createErrorResponse(error).toString();
        }
    }
}
