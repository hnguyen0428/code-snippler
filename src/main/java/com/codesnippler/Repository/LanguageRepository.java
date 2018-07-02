package com.codesnippler.Repository;

import com.codesnippler.Model.Language;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface LanguageRepository extends MongoRepository<Language, String> {
    Language findByName(String name);
}
