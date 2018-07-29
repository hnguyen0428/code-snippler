package com.codesnippler.Controller;

import com.codesnippler.Model.CodeSnippet;
import com.codesnippler.Model.Comment;
import com.codesnippler.Model.Language;
import com.codesnippler.Model.User;
import com.codesnippler.Repository.CodeSnippetRepository;
import com.codesnippler.Repository.CommentRepository;
import com.codesnippler.Repository.LanguageRepository;
import com.codesnippler.Repository.UserRepository;
import com.codesnippler.Exceptions.ErrorTypes;
import com.codesnippler.Utility.GeneralUtility;
import com.codesnippler.Utility.JsonUtility;
import com.codesnippler.Utility.ResponseBuilder;
import com.codesnippler.Utility.StringParser;
import com.codesnippler.Validators.Authorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Stream;


@RestController
@RequestMapping("/api/snippet")
@Validated
public class CodeSnippetController {
    private final CodeSnippetRepository snippetRepo;
    private final UserRepository userRepo;
    private final LanguageRepository langRepo;
    private final CommentRepository commentRepo;
    MongoTemplate mongoTemplate;


    @Autowired
    public CodeSnippetController(CodeSnippetRepository snippetRepo, UserRepository userRepo,
                                 LanguageRepository langRepo, CommentRepository commentRepo,
                                 MongoTemplate mongoTemplate) {
        this.snippetRepo = snippetRepo;
        this.userRepo = userRepo;
        this.langRepo = langRepo;
        this.commentRepo = commentRepo;
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping(produces = "application/json")
    ResponseEntity create(@Authorized HttpServletRequest request,
                          @RequestParam(value = "title") String title,
                          @RequestParam(value = "description", required = false) String description,
                          @RequestParam(value = "code") String code,
                          @RequestParam(value = "language") String languageName) {
        User user = (User)request.getAttribute("authorizedUser");

        Language language = this.langRepo.findByName(languageName);
        if (language == null) {
            String response = ResponseBuilder.createErrorResponse("Invalid Language", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        CodeSnippet snippet = new CodeSnippet(title, description, code, user.getId(), language.getId(), new Date());
        snippet = this.snippetRepo.save(snippet);
        user.addToCreatedSnippets(snippet.getId());
        this.userRepo.save(user);

        String response = ResponseBuilder.createDataResponse(snippet.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{snippetId}", produces = "application/json")
    ResponseEntity update(@Authorized HttpServletRequest request,
                          @RequestParam(value = "title", required = false) String title,
                          @RequestParam(value = "description", required = false) String description,
                          @RequestParam(value = "code", required = false) String code,
                          @RequestParam(value = "language", required = false) String languageName,
                          @PathVariable(value = "snippetId") String snippetId) {
        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        CodeSnippet snippet = snippetOpt.get();
        if (title != null) snippet.setTitle(title);
        if (description != null) snippet.setDescription(description);
        if (code != null) snippet.setCode(code);
        if (languageName != null) {
            Language language = this.langRepo.findByName(languageName);
            if (language == null) {
                String response = ResponseBuilder.createErrorResponse("Unsupported Language Name",
                        ErrorTypes.INV_PARAM_ERROR).toString();
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            snippet.setLanguageId(language.getId());
        }
        snippet = this.snippetRepo.save(snippet);
        String response = ResponseBuilder.createDataResponse(snippet.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping(value = "/{snippetId}", produces = "application/json")
    ResponseEntity delete(@Authorized HttpServletRequest request,
                          @PathVariable(value = "snippetId") String snippetId) {
        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        CodeSnippet snippet = snippetOpt.get();
        User authorizedUser = (User)request.getAttribute("authorizedUser");
        if (!snippet.getUserId().equals(authorizedUser.getId())) {
            String response = ResponseBuilder.createErrorResponse("User is not authorized to delete this snippet",
                    ErrorTypes.INV_AUTH_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<String> commentIds = snippet.getComments();
        commentIds.forEach(this.commentRepo::deleteById);
        this.snippetRepo.delete(snippet);
        authorizedUser.removeFromCreatedSnippets(snippetId);
        authorizedUser.removeFromSavedSnippets(snippetId);

        Set<String> saverIds = snippet.getSavers().keySet();
        List<User> toSaveUsers = new ArrayList<>();
        toSaveUsers.add(authorizedUser);

        saverIds.forEach(id -> {
            Optional<User> userOpt = this.userRepo.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.removeFromSavedSnippets(snippetId);
                toSaveUsers.add(user);
            }
        });
        this.userRepo.saveAll(toSaveUsers);

        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/{snippetId}", produces = "application/json")
    ResponseEntity getSnippet(@PathVariable(value = "snippetId") String snippetId,
                              @RequestParam(value = "increaseViewcount", required = false) boolean shouldIncrease,
                              @RequestParam(value = "showCommentDetails", required = false) boolean showCommentDetails) {
        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        CodeSnippet snippet = snippetOpt.get();

        if (shouldIncrease) {
            snippet.incrementViewsCount();
            snippet = this.snippetRepo.save(snippet);
        }

        // Declare add on key value pairs to the JSON response
        Map<String, String> addOns = new HashMap<>();
        Optional<Language> language = this.langRepo.findById(snippet.getLanguageId());
        if (language.isPresent()) {
            addOns.put("language", language.get().getName());
        }

        JsonObjectBuilder snippetJsonBuilder = snippet.toJsonBuilder(addOns);

        if (showCommentDetails) {
            List<String> commentIds = snippet.getComments();
            Iterable comments = this.commentRepo.findAllById(commentIds);
            JsonArray commentsJsonArray = JsonUtility.listToJson(comments);
            snippetJsonBuilder.add("comments", commentsJsonArray);
        }

        String response = ResponseBuilder.createDataResponse(snippetJsonBuilder.build()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{snippetId}/upvote", produces = "application/json")
    ResponseEntity upvote(@Authorized HttpServletRequest request,
                          @PathVariable(value = "snippetId") String snippetId) {
        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = snippetOpt.get();
        HashMap<String, Boolean> upvoters = snippet.getUpvoters();

        if (!upvoters.containsKey(authorizedUser.getId())) {
            snippet.addToUpvoters(authorizedUser.getId());

            // Remove this user from downvoters if he downvoted the snippet before
            if (snippet.getDownvoters().get(authorizedUser.getId()) != null) {
                snippet.removeFromDownvoters(authorizedUser.getId());
            }

            this.snippetRepo.save(snippet);
            String response = ResponseBuilder.createSuccessResponse().toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            String response = ResponseBuilder.createErrorResponse("User has already upvoted the Snippet", ErrorTypes.INV_REQUEST_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping(value = "/{snippetId}/downvote", produces = "application/json")
    ResponseEntity downvote(@Authorized HttpServletRequest request,
                            @PathVariable(value = "snippetId") String snippetId) {
        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = snippetOpt.get();
        HashMap<String, Boolean> downvoters = snippet.getDownvoters();

        if (!downvoters.containsKey(authorizedUser.getId())) {
            snippet.addToDownvoters(authorizedUser.getId());

            // Remove this user from upvoters if he upvoted the snippet before
            if (snippet.getUpvoters().get(authorizedUser.getId()) != null) {
                snippet.removeFromUpvoters(authorizedUser.getId());
            }

            this.snippetRepo.save(snippet);
            String response = ResponseBuilder.createSuccessResponse().toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            String response = ResponseBuilder.createErrorResponse("User has already downvoted the Snippet", ErrorTypes.INV_REQUEST_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping(value = "/{snippetId}/save", produces = "application/json")
    ResponseEntity saveSnippet(@Authorized HttpServletRequest request,
                               @PathVariable(value = "snippetId") String snippetId) {
        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = snippetOpt.get();
        HashMap<String, Boolean> savedSnippets = authorizedUser.getSavedSnippets();

        if (!savedSnippets.containsKey(snippetId)) {
            authorizedUser.addToSavedSnippets(snippetId);
            snippet.addToSavers(authorizedUser.getId());
            this.snippetRepo.save(snippet);
            this.userRepo.save(authorizedUser);
            String response = ResponseBuilder.createSuccessResponse().toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            String response = ResponseBuilder.createErrorResponse("User has already saved the Snippet", ErrorTypes.INV_REQUEST_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(value = "/{snippetId}/comment", produces = "application/json")
    ResponseEntity createComment(@Authorized HttpServletRequest request,
                                 @RequestParam(value = "content") String content,
                                 @PathVariable(value = "snippetId") String snippetId) {
        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = snippetOpt.get();
        Comment comment = new Comment(content, authorizedUser.getId(), snippetId, new Date());
        comment = this.commentRepo.save(comment);

        snippet.addToComments(comment.getId());
        this.snippetRepo.save(snippet);

        String response = ResponseBuilder.createDataResponse(comment.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/{snippetId}/comments", produces = "application/json")
    ResponseEntity getComments(@PathVariable(value = "snippetId") String snippetId,
                               @RequestParam(value = "showDetails", required = false) boolean showDetails) {
        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(snippetId);
        if (!snippetOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Snippet Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        CodeSnippet snippet = snippetOpt.get();
        List<String> commentIds = snippet.getComments();
        JsonArray data;

        if (showDetails) {
            Iterable comments = this.commentRepo.findAllById(commentIds);
            data = JsonUtility.listToJson(comments);

        }
        else {
            data = JsonUtility.listToJson(commentIds);
        }

        String response = ResponseBuilder.createDataResponse(data).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/popular", produces = "application/json")
    ResponseEntity getPopular(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                              @RequestParam(value = "fields", required = false,
                                      defaultValue = "title,description") String fieldsString) {
        String[] fieldsArray = fieldsString.split(",");
        List<String> fieldsList = new ArrayList<>(Arrays.asList(fieldsArray));

        // Filter out fields to only contain class instance variables
        Set<String> snippetFields = GeneralUtility.getClassInstanceVars(CodeSnippet.class);
        Stream<String> stream = fieldsList.stream().filter(snippetFields::contains);

        Object[] streamArray = stream.toArray();
        // Casting to String array
        fieldsArray = Arrays.copyOf(streamArray, streamArray.length, String[].class);
        Fields fields = Fields.fields(fieldsArray);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project(fields)
                        .andExpression("add(viewsCount, subtract(upvotes, downvotes))")
                        .as("popularityScore"),
                Aggregation.sort(new Sort(Sort.Direction.DESC, "popularityScore")),
                Aggregation.limit(limit)
        );

        AggregationResults<CodeSnippet> results = mongoTemplate.aggregate(aggregation, CodeSnippet.class, CodeSnippet.class);
        List<CodeSnippet> snippets = results.getMappedResults();

        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/mostViews", produces = "application/json")
    ResponseEntity getMostViews(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        PageRequest request = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "viewsCount"));

        Page<CodeSnippet> snippetsPage = this.snippetRepo.findAll(request);
        List snippets = snippetsPage.getContent();

        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/mostUpvotes", produces = "application/json")
    ResponseEntity getMostUpvotes(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        PageRequest request = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "upvotes"));

        Page<CodeSnippet> snippetsPage = this.snippetRepo.findAll(request);
        List snippets = snippetsPage.getContent();

        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/mostSaved", produces = "application/json")
    ResponseEntity getMostSaved(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        PageRequest request = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "savedCount"));

        Page<CodeSnippet> snippetsPage = this.snippetRepo.findAll(request);
        List snippets = snippetsPage.getContent();

        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
