package com.example.blog.adapter.comments;

import com.example.blog.adapter.comments.dto.request.CreateCommentRequest;
import com.example.blog.application.articles.exceptions.ArticleNotFoundException;
import com.example.blog.application.comments.CommentApplicationService;
import com.example.blog.application.comments.exceptions.CommentNotFoundException;
import com.example.blog.domain.comments.Comment;
import com.example.blog.support.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureJsonTesters
@Transactional
@Sql({"classpath:scripts/insert_a_portal_user.sql",
        "classpath:scripts/insert_articles.sql"})
class CommentControllerTest {
    @MockBean
    private CommentApplicationService commentApplicationService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private JacksonTester<Comment> commentJson;

    @Nested
    class saveComment {
        @Test
          void should_save_comment_successfully() throws Exception {
            CreateCommentRequest commentRequest = CreateCommentRequest.builder().content("test").article_id(1L).build();
            Comment comment = Comment.builder().id(1L).content("test").article_id(1L).userName("portal_user").build();
            when(commentApplicationService.save(any(Comment.class))).thenReturn(comment);

            MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                            .post("/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(commentRequest))
                            .cookie(new Cookie("blog_token", jwtUtils.createJwtToken(2L, "PORTAL_USER", "portal_user")))
                    )
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse();

            verify(commentApplicationService).save(any(Comment.class));
            assertEquals(commentJson.write(comment).getJson(), response.getContentAsString());
        }


        @Test
         void should_throw_ArticleNotFoundException_when_article_id_not_exist() throws Exception {
            CreateCommentRequest commentRequest = CreateCommentRequest.builder().content("test").article_id(999L).build();

            doThrow(new ArticleNotFoundException("article")).when(commentApplicationService).save(any(Comment.class));

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(commentRequest))
                            .cookie(new Cookie("blog_token", jwtUtils.createJwtToken(2L, "PORTAL_USER", "portal_user")))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("article不存在"));
        }

    }

    @Nested
    class GetCommentByArticleId {
        @Test
        void should_get_comments_by_article_id() throws Exception {
            Comment comment = Comment.builder().id(1L).content("test").article_id(1L).userName("portal_user").build();

            when(commentApplicationService.getCommentByArticleId(1L)).thenReturn(List.of(comment));

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/comments/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("blog_token", jwtUtils.createJwtToken(2L, "PORTAL_USER", "portal_user")))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].content").value("test"))
                    .andExpect(jsonPath("$[0].userName").value("portal_user"));
        }
        @Test
        void should_throw_CommentNotFoundException_when_comment_not_exist() throws Exception {
            doThrow(new CommentNotFoundException("article999的评论")).when(commentApplicationService).getCommentByArticleId(999L);

            mockMvc.perform(MockMvcRequestBuilders
                            .get("/comments/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("blog_token", jwtUtils.createJwtToken(2L, "PORTAL_USER", "portal_user")))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("article999的评论不存在"));


        }
    }
}
