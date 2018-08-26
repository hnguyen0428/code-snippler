package com.codesnippler.Repository;

import com.codesnippler.Model.CodeSnippet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;


public interface CodeSnippetRepository extends MongoRepository<CodeSnippet, String> {
    @Override
    Page<CodeSnippet> findAll(Pageable pageable);

    Page<CodeSnippet> findByLanguageName(String languageName, Pageable pageable);

    Page<CodeSnippet> findByLanguageNameMatchesRegex(String regex, Pageable pageable);
}
