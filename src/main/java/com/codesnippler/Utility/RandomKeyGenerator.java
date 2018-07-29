package com.codesnippler.Utility;

import com.codesnippler.Model.User;
import com.codesnippler.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.SecureRandom;
import java.util.Base64;


public class RandomKeyGenerator {
    private final UserRepository userRepo;


    // Inject user repo in to allow this class to check for uniqueness of Api Key
    public RandomKeyGenerator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public String generateApiKey(final int length) {
        SecureRandom random = new SecureRandom();
        String apiKey;
        User user;

        do {
            byte bytes[] = new byte[length];
            random.nextBytes(bytes);
            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            apiKey = encoder.encodeToString(bytes);
            user = userRepo.findByApiKey(apiKey);
        } while (user != null);

        return apiKey;
    }
}
