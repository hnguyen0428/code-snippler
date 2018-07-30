package com.codesnippler.Controller;

import com.codesnippler.Model.*;
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
import com.codesnippler.Validators.ValidSnippet;
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

import javax.json.*;
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
    private final MongoTemplate mongoTemplate;


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
                          @PathVariable(value = "snippetId") @ValidSnippet String snippetId) {
        CodeSnippet snippet = (CodeSnippet)request.getAttribute("validSnippet");

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
                          @PathVariable(value = "snippetId") @ValidSnippet String snippetId) {
        CodeSnippet snippet = (CodeSnippet)request.getAttribute("validSnippet");
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
    ResponseEntity getSnippet(@Authorized(required = false) HttpServletRequest request,
                              @PathVariable(value = "snippetId") @ValidSnippet String snippetId,
                              @RequestParam(value = "increaseViewcount", required = false) boolean shouldIncrease,
                              @RequestParam(value = "showCommentDetails", required = false) boolean showCommentDetails) {
        CodeSnippet snippet = (CodeSnippet)request.getAttribute("validSnippet");

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

        User authorizedUser = (User)request.getAttribute("authorizedUser");
        if (authorizedUser != null) {
            snippet.setUpvoted(authorizedUser);
            snippet.setDownvoted(authorizedUser);
            snippet.setSaved(authorizedUser);
        }
        snippet.setPopularityScore();

        JsonObjectBuilder snippetJsonBuilder = snippet.toJsonBuilder(addOns);

        if (showCommentDetails) {
            List<String> commentIds = snippet.getComments();
            Iterable<Comment> comments = this.commentRepo.findAllById(commentIds);

            List<JsonObject> jsons = new ArrayList<>();
            comments.forEach(comment -> {
                if (authorizedUser != null) {
                    comment.setUpvoted(authorizedUser);
                    comment.setDownvoted(authorizedUser);
                }
                jsons.add(comment.toJson());
            });

            JsonArray commentsJsonArray = JsonUtility.listToJson(jsons);
            snippetJsonBuilder.add("comments", commentsJsonArray);
        }

        String response = ResponseBuilder.createDataResponse(snippetJsonBuilder.build()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{snippetId}/upvote", produces = "application/json")
    ResponseEntity upvote(@Authorized HttpServletRequest request,
                          @PathVariable(value = "snippetId") @ValidSnippet String snippetId,
                          @RequestParam(value = "upvote") boolean upvote) {
        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = (CodeSnippet)request.getAttribute("validSnippet");
        HashMap<String, Boolean> upvoters = snippet.getUpvoters();

        if (upvote) {
            if (!upvoters.containsKey(authorizedUser.getId())) {
                snippet.addToUpvoters(authorizedUser.getId());

                // Remove this user from downvoters if he downvoted the snippet before
                if (snippet.getDownvoters().get(authorizedUser.getId()) != null) {
                    snippet.removeFromDownvoters(authorizedUser.getId());
                }

                this.snippetRepo.save(snippet);
            }
        }
        else {
            if (upvoters.containsKey(authorizedUser.getId())) {
                snippet.removeFromUpvoters(authorizedUser.getId());
                this.snippetRepo.save(snippet);
            }
        }
        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{snippetId}/downvote", produces = "application/json")
    ResponseEntity downvote(@Authorized HttpServletRequest request,
                            @PathVariable(value = "snippetId") @ValidSnippet String snippetId,
                            @RequestParam(value = "downvote") boolean downvote) {
        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = (CodeSnippet)request.getAttribute("validSnippet");
        HashMap<String, Boolean> downvoters = snippet.getDownvoters();

        if (downvote) {
            if (!downvoters.containsKey(authorizedUser.getId())) {
                snippet.addToDownvoters(authorizedUser.getId());

                // Remove this user from upvoters if he upvoted the snippet before
                if (snippet.getUpvoters().get(authorizedUser.getId()) != null) {
                    snippet.removeFromUpvoters(authorizedUser.getId());
                }

                this.snippetRepo.save(snippet);
            }
        }
        else {
            if (downvoters.containsKey(authorizedUser.getId())) {
                snippet.removeFromDownvoters(authorizedUser.getId());
                this.snippetRepo.save(snippet);
            }
        }
        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{snippetId}/save", produces = "application/json")
    ResponseEntity saveSnippet(@Authorized HttpServletRequest request,
                               @PathVariable(value = "snippetId") @ValidSnippet String snippetId,
                               @RequestParam(value = "save") boolean save) {
        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = (CodeSnippet)request.getAttribute("validSnippet");
        HashMap<String, Boolean> savedSnippets = authorizedUser.getSavedSnippets();

        if (save) {
            if (!savedSnippets.containsKey(snippetId)) {
                authorizedUser.addToSavedSnippets(snippetId);
                snippet.addToSavers(authorizedUser.getId());
                this.snippetRepo.save(snippet);
                this.userRepo.save(authorizedUser);
            }
        }
        else {
            if (savedSnippets.containsKey(snippetId)) {
                authorizedUser.removeFromSavedSnippets(snippetId);
                snippet.removeFromSavers(authorizedUser.getId());
                this.snippetRepo.save(snippet);
                this.userRepo.save(authorizedUser);
            }
        }
        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/{snippetId}/comment", produces = "application/json")
    ResponseEntity createComment(@Authorized HttpServletRequest request,
                                 @RequestParam(value = "content") String content,
                                 @PathVariable(value = "snippetId") @ValidSnippet String snippetId) {
        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = (CodeSnippet)request.getAttribute("validSnippet");
        Comment comment = new Comment(content, authorizedUser.getId(), snippetId, new Date());
        comment = this.commentRepo.save(comment);

        snippet.addToComments(comment.getId());
        this.snippetRepo.save(snippet);

        String response = ResponseBuilder.createDataResponse(comment.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/{snippetId}/comments", produces = "application/json")
    ResponseEntity getComments(@Authorized(required = false) HttpServletRequest request,
                               @PathVariable(value = "snippetId") @ValidSnippet String snippetId,
                               @RequestParam(value = "showUserDetails", required = false) boolean showUserDetails,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User authorizedUser = (User)request.getAttribute("authorizedUser");

        CodeSnippet snippet = (CodeSnippet)request.getAttribute("validSnippet");
        List<String> commentIds = snippet.getComments();
        // Pagination
        commentIds = GeneralUtility.paginate(commentIds, page, pageSize);

        JsonArray data;

        Iterable<Comment> comments = this.commentRepo.findAllById(commentIds);

        List<JsonObject> jsons = new ArrayList<>();
        if (showUserDetails) {
            Map<String, User> usersMap = new HashMap<>();
            List<String> usersToFind = new ArrayList<>();
            comments.forEach(comment -> {
                String userId = comment.getUserId();
                usersToFind.add(userId);
            });
            Iterable<User> users = this.userRepo.findAllById(usersToFind);
            users.forEach(user -> usersMap.put(user.getId(), user));

            comments.forEach(comment -> {
                if (authorizedUser != null) {
                    comment.setUpvoted(authorizedUser);
                    comment.setDownvoted(authorizedUser);
                }
                JsonObjectBuilder builder = comment.toJsonBuilder();
                User user = usersMap.get(comment.getUserId());
                builder.add("user", user.toJson());

                jsons.add(builder.build());
            });
            data = JsonUtility.listToJson(jsons);
        }
        else {
            comments.forEach(comment -> {
                if (authorizedUser != null) {
                    comment.setUpvoted(authorizedUser);
                    comment.setDownvoted(authorizedUser);
                }
                jsons.add(comment.toJson());
            });
            data = JsonUtility.listToJson(comments);
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
        Set<String> snippetFields = JsonModel.getModelAttributes(CodeSnippet.class);
        Stream<String> stream = fieldsList.stream().filter(snippetFields::contains);

        Object[] streamArray = stream.toArray();
        // Casting to String array
        fieldsArray = Arrays.copyOf(streamArray, streamArray.length, String[].class);
        Fields fields = Fields.fields(fieldsArray);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project(fields)
                        .andExpression("add(add(viewsCount, subtract(upvotes, downvotes)), savedCount)")
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
