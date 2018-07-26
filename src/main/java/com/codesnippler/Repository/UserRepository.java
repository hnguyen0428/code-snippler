package com.codesnippler.Repository;

import com.codesnippler.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    User findByApiKey(String apiKey);
}
