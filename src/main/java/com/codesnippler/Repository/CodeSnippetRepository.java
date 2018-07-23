package com.codesnippler.Repository;

import com.codesnippler.Model.CodeSnippet;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CodeSnippetRepository extends MongoRepository<CodeSnippet, String> {
}
