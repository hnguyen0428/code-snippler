package com.codesnippler.Controller;


import com.codesnippler.Exceptions.ErrorTypes;
import com.codesnippler.Model.CodeSnippet;
import com.codesnippler.Model.Comment;
import com.codesnippler.Model.User;
import com.codesnippler.Repository.CodeSnippetRepository;
import com.codesnippler.Repository.CommentRepository;
import com.codesnippler.Utility.ResponseBuilder;
import com.codesnippler.Validators.Authorized;
import com.codesnippler.Validators.ValidComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentRepository commentRepo;
    private final CodeSnippetRepository snippetRepo;


    @Autowired
    public CommentController(CommentRepository commentRepo, CodeSnippetRepository snippetRepo) {
        this.commentRepo = commentRepo;
        this.snippetRepo = snippetRepo;
    }


    @PatchMapping(value = "/{commentId}", produces = "application/json")
    ResponseEntity update(HttpServletRequest request,
                          @Authorized User authorizedUser,
                          @RequestParam(value = "content") String content,
                          @PathVariable(value = "commentId") @ValidComment String commentId) {
        Comment comment = (Comment)request.getAttribute("validComment");

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
    ResponseEntity delete(HttpServletRequest request,
                          @Authorized User authorizedUser,
                          @PathVariable(value = "commentId") @ValidComment String commentId) {
        Comment comment = (Comment)request.getAttribute("validComment");

        if (!comment.getUserId().equals(authorizedUser.getId())) {
            String response = ResponseBuilder.createErrorResponse("User is not authorized to update this comment",
                    ErrorTypes.INV_AUTH_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<CodeSnippet> snippetOpt = this.snippetRepo.findById(comment.getSnippetId());

        if (snippetOpt.isPresent()) {
            CodeSnippet snippet = snippetOpt.get();
            snippet.removeFromComments(commentId);
            this.snippetRepo.save(snippet);
        }
        this.commentRepo.delete(comment);

        String response = ResponseBuilder.createSuccessResponse().toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PatchMapping(value = "/{commentId}/upvote", produces = "application/json")
    ResponseEntity upvote(HttpServletRequest request,
                          @Authorized User authorizedUser,
                          @PathVariable(value = "commentId") @ValidComment String commentId,
                          @RequestParam(value = "upvote") boolean upvote) {
        Comment comment = (Comment)request.getAttribute("validComment");

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
    ResponseEntity downvote(HttpServletRequest request,
                            @Authorized User authorizedUser,
                            @PathVariable(value = "commentId") @ValidComment String commentId,
                            @RequestParam(value = "downvote") boolean downvote) {
        Comment comment = (Comment)request.getAttribute("validComment");

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
