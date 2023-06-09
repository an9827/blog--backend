package com.blog.adapter.comments;

import com.blog.adapter.comments.aspect.GetUserIdFromCookies;
import com.blog.adapter.comments.dto.request.CreateCommentRequest;
import com.blog.adapter.comments.mapper.CommentDtoMapper;
import com.blog.application.comments.CommentApplicationService;
import com.blog.application.user.UserApplicationService;
import com.blog.domain.comments.Comment;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@Validated
@AllArgsConstructor
@Transactional
@io.swagger.v3.oas.annotations.tags.Tag(name = "Comment API")
public class CommentController {
    private final CommentApplicationService commentApplicationService;
    private final UserApplicationService userApplicationService;

    @PostMapping
    @GetUserIdFromCookies
    @Operation(summary = "Save comment")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@RequestBody @Valid CreateCommentRequest commentRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = userApplicationService.findUserNameByUserId(userId);

        Comment comment = CommentDtoMapper.MAPPER.toModel(commentRequest, userName);
        return commentApplicationService.save(comment);
    }

    @GetMapping("/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "get comments by article id")
    public Page<Comment> getCommentByArticleId(@PathVariable Long articleId,
                                               @RequestParam(required = false, defaultValue = "DESC") String orderBy,
                                               @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                               @RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "10") int size) {
        return commentApplicationService.getCommentByArticleId(articleId, orderBy, sortBy, page, size);
    }
}
