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
import com.codesnippler.Validators.ValidLanguageName;
import com.codesnippler.Validators.ValidSnippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.*;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@CrossOrigin
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
    ResponseEntity create(HttpServletRequest request,
                          @Authorized User authorizedUser,
                          @RequestParam(value = "title") String title,
                          @RequestParam(value = "description", required = false) String description,
                          @RequestParam(value = "code") String code,
                          @RequestParam(value = "language") @ValidLanguageName String languageName) {
        Language language = (Language) request.getAttribute("validLanguage");

        CodeSnippet snippet = new CodeSnippet(title, description, code, authorizedUser.getId(), language.getName(), new Date());
        snippet = this.snippetRepo.save(snippet);
        authorizedUser.addToCreatedSnippets(snippet.getId());
        this.userRepo.save(authorizedUser);

        String response = ResponseBuilder.createDataResponse(snippet.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/{snippetId}/update", produces = "application/json")
    ResponseEntity update(HttpServletRequest request,
                          @Authorized User authorizedUser,
                          @RequestParam(value = "title", required = false) String title,
                          @RequestParam(value = "description", required = false) String description,
                          @RequestParam(value = "code", required = false) String code,
                          @RequestParam(value = "language", required = false) @ValidLanguageName String languageName,
                          @PathVariable(value = "snippetId")
                          @NotNull(message = "Invalid Snippet ID") CodeSnippet snippet) {
        if (!snippet.getUserId().equals(authorizedUser.getId())) {
            String response = ResponseBuilder.createErrorResponse("User is not authorized to delete this snippet",
                    ErrorTypes.INV_AUTH_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (title != null) snippet.setTitle(title);
        if (description != null) snippet.setDescription(description);
        if (code != null) snippet.setCode(code);
        if (languageName != null) {
            Language language = (Language) request.getAttribute("validLanguage");
            snippet.setLanguageName(language.getName());
        }
        snippet = this.snippetRepo.save(snippet);
        String response = ResponseBuilder.createDataResponse(snippet.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping(value = "/{snippetId}", produces = "application/json")
    ResponseEntity delete(@Authorized User authorizedUser,
                          @PathVariable(value = "snippetId")
                          @NotNull(message = "Invalid Snippet ID") CodeSnippet snippet) {
        if (!snippet.getUserId().equals(authorizedUser.getId())) {
            String response = ResponseBuilder.createErrorResponse("User is not authorized to delete this snippet",
                    ErrorTypes.INV_AUTH_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<String> commentIds = snippet.getComments();
        commentIds.forEach(this.commentRepo::deleteById);
        this.snippetRepo.delete(snippet);
        authorizedUser.removeFromCreatedSnippets(snippet.getId());
        authorizedUser.removeFromSavedSnippets(snippet.getId());

        Set<String> saverIds = snippet.getSavers().keySet();
        List<User> toSaveUsers = new ArrayList<>();
        toSaveUsers.add(authorizedUser);

        saverIds.forEach(id -> {
            Optional<User> userOpt = this.userRepo.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.removeFromSavedSnippets(snippet.getId());
                toSaveUsers.add(user);
            }
        });
        this.userRepo.saveAll(toSaveUsers);

        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/{snippetId}", produces = "application/json")
    ResponseEntity getSnippet(@Authorized(required = false) User authorizedUser,
                              @PathVariable(value = "snippetId")
                              @NotNull(message = "Invalid Snippet ID") CodeSnippet snippet,
                              @RequestParam(value = "increaseViewcount", required = false) boolean shouldIncrease,
                              @RequestParam(value = "showCommentDetails", required = false) boolean showCommentDetails,
                              @RequestParam(value = "showUserDetails", required = false) boolean showUserDetails) {
        if (shouldIncrease) {
            snippet.incrementViewsCount();
            snippet = this.snippetRepo.save(snippet);
        }

        if (authorizedUser.isAuthenticated()) {
            snippet.setUserRelatedStatus(authorizedUser);
        }
        snippet.setPopularityScore();

        if (showCommentDetails) {
            snippet.includeCommentsDetails(commentRepo, authorizedUser);
        }

        if (showUserDetails) {
            snippet.includeUserDetails(userRepo);
        }

        String response = ResponseBuilder.createDataResponse(snippet.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/byIds", produces = "application/json")
    ResponseEntity getSnippets(@Authorized(required = false) User authorizedUser,
                               @RequestParam(value = "ids") List<String> snippetIds,
                               @RequestParam(value = "showUserDetails", required = false) boolean showUserDetails) {
        Iterable<CodeSnippet> snippets = this.snippetRepo.findAllById(snippetIds);

        if (authorizedUser.isAuthenticated())
            for (CodeSnippet snippet : snippets)
                snippet.setUserRelatedStatus(authorizedUser);

        if (!showUserDetails) {
            JsonArray snippetsJson = JsonUtility.listToJson(snippets);
            String response = ResponseBuilder.createDataResponse(snippetsJson).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        List<String> userIds = new ArrayList<>();
        for (CodeSnippet snippet : snippets)
            userIds.add(snippet.getUserId());

        Iterable<User> users = this.userRepo.findAllById(userIds);
        Map<String, User> usersMap = new HashMap<>();
        for (User user : users)
            usersMap.put(user.getId(), user);

        for (CodeSnippet snippet : snippets)
            snippet.includeInJson("user", usersMap.get(snippet.getUserId()));


        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/byLanguage", produces = "application/json")
    ResponseEntity getSnippets(HttpServletRequest request,
                               @Authorized(required = false) User authorizedUser,
                               @RequestParam(value = "language") @ValidLanguageName String languageName,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(value = "fields", required = false,
                                       defaultValue = "title,description,upvotes,downvotes,viewsCount,savedCount," +
                                               "languageName,savers,upvoters,downvoters") String fieldsString) {
        languageName = ((Language) (request.getAttribute("validLanguage"))).getName();
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
                Aggregation.match(Criteria.where("languageName").is(languageName)),
                Aggregation.project(fields)
                        .andExpression("add(add(viewsCount, subtract(upvotes, downvotes)), savedCount)")
                        .as("popularityScore"),
                Aggregation.sort(new Sort(Sort.Direction.DESC, "popularityScore")),
                Aggregation.skip((long)page * pageSize),
                Aggregation.limit(pageSize)
        );

        AggregationResults<CodeSnippet> results = mongoTemplate.aggregate(aggregation, CodeSnippet.class, CodeSnippet.class);
        List<CodeSnippet> snippets = results.getMappedResults();

        if (authorizedUser.isAuthenticated())
            for (CodeSnippet snippet: snippets)
                snippet.setUserRelatedStatus(authorizedUser);

        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{snippetId}/upvote", produces = "application/json")
    ResponseEntity upvote(HttpServletRequest request,
                          @Authorized User authorizedUser,
                          @PathVariable(value = "snippetId")
                          @NotNull(message = "Invalid Snippet ID") CodeSnippet snippet,
                          @RequestParam(value = "upvote") boolean upvote) {
        Map<String, Boolean> upvoters = snippet.getUpvoters();

        if (upvote) {
            if (!upvoters.containsKey(authorizedUser.getId())) {
                snippet.addToUpvoters(authorizedUser.getId());

                // Remove this user from downvoters if he downvoted the snippet before
                if (snippet.getDownvoters().get(authorizedUser.getId()) != null) {
                    snippet.removeFromDownvoters(authorizedUser.getId());
                }

                this.snippetRepo.save(snippet);
            }
        } else {
            if (upvoters.containsKey(authorizedUser.getId())) {
                snippet.removeFromUpvoters(authorizedUser.getId());
                this.snippetRepo.save(snippet);
            }
        }
        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{snippetId}/downvote", produces = "application/json")
    ResponseEntity downvote(@Authorized User authorizedUser,
                            @PathVariable(value = "snippetId")
                            @NotNull(message = "Invalid Snippet ID") CodeSnippet snippet,
                            @RequestParam(value = "downvote") boolean downvote) {
        Map<String, Boolean> downvoters = snippet.getDownvoters();

        if (downvote) {
            if (!downvoters.containsKey(authorizedUser.getId())) {
                snippet.addToDownvoters(authorizedUser.getId());

                // Remove this user from upvoters if he upvoted the snippet before
                if (snippet.getUpvoters().get(authorizedUser.getId()) != null) {
                    snippet.removeFromUpvoters(authorizedUser.getId());
                }

                this.snippetRepo.save(snippet);
            }
        } else {
            if (downvoters.containsKey(authorizedUser.getId())) {
                snippet.removeFromDownvoters(authorizedUser.getId());
                this.snippetRepo.save(snippet);
            }
        }
        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{snippetId}/save", produces = "application/json")
    ResponseEntity saveSnippet(@Authorized User authorizedUser,
                               @PathVariable(value = "snippetId")
                               @NotNull(message = "Invalid Snippet ID") CodeSnippet snippet,
                               @RequestParam(value = "save") boolean save) {
        Map<String, Boolean> savedSnippets = authorizedUser.getSavedSnippets();

        if (save) {
            if (!savedSnippets.containsKey(snippet.getId())) {
                authorizedUser.addToSavedSnippets(snippet.getId());
                snippet.addToSavers(authorizedUser.getId());
                this.snippetRepo.save(snippet);
                this.userRepo.save(authorizedUser);
            }
        } else {
            if (savedSnippets.containsKey(snippet.getId())) {
                authorizedUser.removeFromSavedSnippets(snippet.getId());
                snippet.removeFromSavers(authorizedUser.getId());
                this.snippetRepo.save(snippet);
                this.userRepo.save(authorizedUser);
            }
        }
        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/{snippetId}/comment", produces = "application/json")
    ResponseEntity createComment(@Authorized User authorizedUser,
                                 @RequestParam(value = "content") String content,
                                 @PathVariable(value = "snippetId")
                                 @NotNull(message = "Invalid Snippet ID") CodeSnippet snippet) {
        Comment comment = new Comment(content, authorizedUser.getId(), snippet.getId(), new Date());
        comment = this.commentRepo.save(comment);

        snippet.addToComments(comment.getId());
        this.snippetRepo.save(snippet);

        String response = ResponseBuilder.createDataResponse(comment.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/{snippetId}/comments", produces = "application/json")
    ResponseEntity getComments(@Authorized(required = false) User authorizedUser,
                               @PathVariable(value = "snippetId")
                               @NotNull(message = "Invalid Snippet ID") CodeSnippet snippet,
                               @RequestParam(value = "showUserDetails", required = false) boolean showUserDetails,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        List<String> commentIds = snippet.getComments();
        commentIds = GeneralUtility.paginate(commentIds, page, pageSize);

        JsonArray data;

        Iterable<Comment> comments = this.commentRepo.findAllById(commentIds);

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
                comment.setUserRelatedStatus(authorizedUser);
                User user = usersMap.get(comment.getUserId());
                comment.includeInJson("user", user);
            });
            data = JsonUtility.listToJson(comments);
        } else {
            comments.forEach(comment -> {
                comment.setUserRelatedStatus(authorizedUser);
            });
            data = JsonUtility.listToJson(comments);
        }

        String response = ResponseBuilder.createDataResponse(data).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/search", produces = "application/json")
    ResponseEntity search(@Authorized(required = false) User authorizedUser,
                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                          @RequestParam(value = "query") String query,
                          @RequestParam(value = "fields", required = false,
                                  defaultValue = "title,description,upvotes,downvotes,viewsCount,savedCount," +
                                          "languageName,savers,upvoters,downvoters") String fieldsString) {
        String[] fieldsArray = fieldsString.split(",");
        List<String> fieldsList = new ArrayList<>(Arrays.asList(fieldsArray));

        // Filter out fields to only contain class instance variables
        Set<String> snippetFields = JsonModel.getModelAttributes(CodeSnippet.class);
        Stream<String> stream = fieldsList.stream().filter(snippetFields::contains);

        Object[] streamArray = stream.toArray();
        // Casting to String array
        fieldsArray = Arrays.copyOf(streamArray, streamArray.length, String[].class);
        Fields fields = Fields.fields(fieldsArray);

        List<String> words = Arrays.asList(query.split(" "));
        List<String> regexes = words.stream().map(word -> String.format(".*%s.*", word)).collect(Collectors.toList());

        String regex = String.join("|", regexes);
        Pattern regPat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Criteria matchQuery = new Criteria();
        matchQuery.orOperator(Criteria.where("title").regex(regPat), Criteria.where("description").regex(regPat));

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(matchQuery),
                Aggregation.project(fields)
                        .andExpression("add(add(viewsCount, subtract(upvotes, downvotes)), savedCount)")
                        .as("popularityScore"),
                Aggregation.sort(new Sort(Sort.Direction.DESC, "popularityScore")),
                Aggregation.skip((long)page * pageSize),
                Aggregation.limit(pageSize)
        );

        AggregationResults<CodeSnippet> results = mongoTemplate.aggregate(aggregation, CodeSnippet.class, CodeSnippet.class);
        List<CodeSnippet> snippets = results.getMappedResults();

        if (authorizedUser.isAuthenticated())
            for (CodeSnippet snippet: snippets)
                snippet.setUserRelatedStatus(authorizedUser);

        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/popular", produces = "application/json")
    ResponseEntity getPopular(@Authorized(required = false) User authorizedUser,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                              @RequestParam(value = "fields", required = false,
                                      defaultValue = "title,description,upvotes,downvotes,viewsCount,savedCount," +
                                              "languageName,savers,upvoters,downvoters") String fieldsString) {
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
                Aggregation.skip((long)page * pageSize),
                Aggregation.limit(pageSize)
        );

        AggregationResults<CodeSnippet> results = mongoTemplate.aggregate(aggregation, CodeSnippet.class, CodeSnippet.class);
        List<CodeSnippet> snippets = results.getMappedResults();

        if (authorizedUser.isAuthenticated())
            for (CodeSnippet snippet: snippets)
                snippet.setUserRelatedStatus(authorizedUser);


        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/mostViews", produces = "application/json")
    ResponseEntity getMostViews(@Authorized(required = false) User authorizedUser,
                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        PageRequest request = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "viewsCount"));

        Page<CodeSnippet> snippetsPage = this.snippetRepo.findAll(request);
        List<CodeSnippet> snippets = snippetsPage.getContent();

        if (authorizedUser.isAuthenticated())
            for (CodeSnippet snippet: snippets)
                snippet.setUserRelatedStatus(authorizedUser);

        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/mostUpvotes", produces = "application/json")
    ResponseEntity getMostUpvotes(@Authorized(required = false) User authorizedUser,
                                  @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        PageRequest request = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "upvotes"));

        Page<CodeSnippet> snippetsPage = this.snippetRepo.findAll(request);
        List<CodeSnippet> snippets = snippetsPage.getContent();

        if (authorizedUser.isAuthenticated())
            for (CodeSnippet snippet: snippets)
                snippet.setUserRelatedStatus(authorizedUser);

        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(value = "/mostSaved", produces = "application/json")
    ResponseEntity getMostSaved(@Authorized(required = false) User authorizedUser,
                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        PageRequest request = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "savedCount"));

        Page<CodeSnippet> snippetsPage = this.snippetRepo.findAll(request);
        List<CodeSnippet> snippets = snippetsPage.getContent();

        if (authorizedUser.isAuthenticated())
            for (CodeSnippet snippet: snippets)
                snippet.setUserRelatedStatus(authorizedUser);

        JsonArray snippetsJson = JsonUtility.listToJson(snippets);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
