package com.codesnippler.Controller;


import com.codesnippler.Exceptions.ErrorTypes;
import com.codesnippler.Model.CodeSnippet;
import com.codesnippler.Model.Comment;
import com.codesnippler.Model.User;
import com.codesnippler.Repository.CodeSnippetRepository;
import com.codesnippler.Repository.CommentRepository;
import com.codesnippler.Utility.ResponseBuilder;
import com.codesnippler.Validators.Authorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    ResponseEntity update(@Authorized HttpServletRequest request,
                          @RequestParam(value = "content") String content,
                          @PathVariable(value = "commentId") String commentId) {
        Optional<Comment> commentOpt = this.commentRepo.findById(commentId);
        if (!commentOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Comment Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User authorizedUser = (User)request.getAttribute("authorizedUser");
        Comment comment = commentOpt.get();

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
    ResponseEntity delete(@Authorized HttpServletRequest request,
                          @PathVariable(value = "commentId") String commentId) {
        Optional<Comment> commentOpt = this.commentRepo.findById(commentId);
        if (!commentOpt.isPresent()) {
            String response = ResponseBuilder.createErrorResponse("Invalid Comment Id", ErrorTypes.INV_PARAM_ERROR).toString();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User authorizedUser = (User)request.getAttribute("authorizedUser");
        Comment comment = commentOpt.get();

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
}
