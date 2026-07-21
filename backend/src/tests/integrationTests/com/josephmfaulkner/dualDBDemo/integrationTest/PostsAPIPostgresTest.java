package com.josephmfaulkner.dualDBDemo.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.josephmfaulkner.dualDBDemo.posts.dto.Post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(PostgresTestConfiguration.class)
public class PostsAPIPostgresTest {
    @Autowired
    private MockMvcTester mockMvcTester;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateAndGetPost() throws Exception {
        Post postToCreate = Post.builder().title("My First Post").content("Hello, World!").build();

        assertThat(
                this.mockMvcTester.perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postToCreate))
                )
        ).hasStatus(HttpStatus.CREATED);
    }
}
