package com.codesnippler.Controller;

import com.codesnippler.Model.User;
import com.codesnippler.Repository.UserRepository;
import com.codesnippler.Utility.RandomKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {
    public static final int API_KEY_LENGTH = 80;

    private final UserRepository userRepo;

    @Autowired
    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/register")
    User add(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPw = encoder.encode(password);

        String apiKey = RandomKeyGenerator.generateApiKey(API_KEY_LENGTH);

        User newUser = this.userRepo.save(new User(username, hashedPw, apiKey, new Date()));
        return newUser;
    }

//    @PostMapping("/login")
//    User login(HttpServletRequest request) {
//
//    }
}
