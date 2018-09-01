package com.codesnippler.Controller;


import com.codesnippler.Exceptions.ErrorTypes;
import com.codesnippler.Model.CodeSnippet;
import com.codesnippler.Model.Comment;
import com.codesnippler.Model.User;
import com.codesnippler.Repository.CodeSnippetRepository;
import com.codesnippler.Repository.CommentRepository;
import com.codesnippler.Repository.UserRepository;
import com.codesnippler.Utility.JsonUtility;
import com.codesnippler.Utility.ResponseBuilder;
import com.codesnippler.Validators.Authorized;
import com.codesnippler.Validators.ClientAuthorized;
import com.codesnippler.Validators.ValidComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentRepository commentRepo;
    private final CodeSnippetRepository snippetRepo;
    private final UserRepository userRepo;


    @Autowired
    public CommentController(CommentRepository commentRepo, CodeSnippetRepository snippetRepo,
                             UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.snippetRepo = snippetRepo;
        this.userRepo = userRepo;
    }


    @GetMapping(value = "/byIds", produces = "application/json")
    ResponseEntity getComments(@Authorized(required = false) User authorizedUser,
                               @RequestParam(value = "ids") List<String> commentIds,
                               @RequestParam(value = "showUserDetails", required = false) boolean showUserDetails) {
        Iterable<Comment> comments = this.commentRepo.findAllById(commentIds);

        if (authorizedUser.isAuthenticated())
            for (Comment comment : comments)
                comment.setUserRelatedStatus(authorizedUser);

        if (!showUserDetails) {
            JsonArray snippetsJson = JsonUtility.listToJson(comments);
            String response = ResponseBuilder.createDataResponse(snippetsJson).toString();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        List<String> userIds = new ArrayList<>();
        for (Comment comment : comments)
            userIds.add(comment.getUserId());

        Iterable<User> users = this.userRepo.findAllById(userIds);
        Map<String, User> usersMap = new HashMap<>();
        for (User user : users)
            usersMap.put(user.getId(), user);

        for (Comment comment : comments)
            comment.includeInJson("user", usersMap.get(comment.getUserId()));


        JsonArray snippetsJson = JsonUtility.listToJson(comments);
        String response = ResponseBuilder.createDataResponse(snippetsJson).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{commentId}", produces = "application/json")
    @ClientAuthorized
    ResponseEntity update(@Authorized User authorizedUser,
                          @RequestParam(value = "content") @Size(min = 1, max = 1500) String content,
                          @PathVariable(value = "commentId") @NotNull(message = "Invalid Comment ID") Comment comment) {
        if (!comment.getUserId().equals(authorizedUser.getId())) {
            String response = ResponseBuilder.createErrorResponse("User is not authorized to update this comment",
                    ErrorTypes.INV_AUTH_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        comment.setContent(content);
        comment = this.commentRepo.save(comment);
        String response = ResponseBuilder.createDataResponse(comment.toJson()).toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping(value = "/{commentId}", produces = "application/json")
    @ClientAuthorized
    ResponseEntity delete(@Authorized User authorizedUser,
                          @PathVariable(value = "commentId") @NotNull(message = "Invalid Comment ID") Comment comment) {
        if (!comment.getUserId().equals(authorizedUser.getId())) {
            String response = ResponseBuilder.createErrorResponse("User is not authorized to update this comment",
                    ErrorTypes.INV_AUTH_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(comment.getSnippetId());

        if (snippetOpt.isPresent()) {
            CodeSnippet snippet = snippetOpt.get();
            snippet.removeFromComments(comment.getId());
            this.snippetRepo.save(snippet);
        }
        this.commentRepo.delete(comment);

        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{commentId}/upvote", produces = "application/json")
    @ClientAuthorized
    ResponseEntity upvote(@Authorized User authorizedUser,
                          @PathVariable(value = "commentId") @NotNull(message = "Invalid Comment ID") Comment comment,
                          @RequestParam(value = "upvote") boolean upvote) {
        HashMap<String, Boolean> upvoters = comment.getUpvoters();

        if (upvote) {
            if (!upvoters.containsKey(authorizedUser.getId())) {
                comment.addToUpvoters(authorizedUser.getId());

                // Remove this user from downvoters if he downvoted the snippet before
                if (comment.getDownvoters().get(authorizedUser.getId()) != null) {
                    comment.removeFromDownvoters(authorizedUser.getId());
                }

                this.commentRepo.save(comment);
            }
        }
        else {
            if (upvoters.containsKey(authorizedUser.getId())) {
                comment.removeFromUpvoters(authorizedUser.getId());
                this.commentRepo.save(comment);
            }
        }

        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{commentId}/downvote", produces = "application/json")
    @ClientAuthorized
    ResponseEntity downvote(@Authorized User authorizedUser,
                            @PathVariable(value = "commentId") @NotNull(message = "Invalid Comment ID") Comment comment,
                            @RequestParam(value = "downvote") boolean downvote) {
        HashMap<String, Boolean> downvoters = comment.getDownvoters();

        if (downvote) {
            if (!downvoters.containsKey(authorizedUser.getId())) {
                comment.addToDownvoters(authorizedUser.getId());

                // Remove this user from downvoters if he downvoted the snippet before
                if (comment.getUpvoters().get(authorizedUser.getId()) != null) {
                    comment.removeFromUpvoters(authorizedUser.getId());
                }

                this.commentRepo.save(comment);
            }
        }
        else {
            if (downvoters.containsKey(authorizedUser.getId())) {
                comment.removeFromDownvoters(authorizedUser.getId());
                this.commentRepo.save(comment);
            }
        }
        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
