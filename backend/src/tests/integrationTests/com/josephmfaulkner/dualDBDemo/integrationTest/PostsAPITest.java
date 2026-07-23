package com.josephmfaulkner.dualDBDemo.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephmfaulkner.dualDBDemo.testFixtures._BaseDualDbTest;
import com.josephmfaulkner.dualDBDemo.integrationTest.config.DualWriteDbTestConfiguration;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.models.PostEntity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.context.ActiveProfiles;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(DualWriteDbTestConfiguration.class)
public class PostsAPITest extends _BaseDualDbTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DynamoDbEnhancedClient enhancedClient;

    private DynamoDbTable<PostEntity> postTable;

    @BeforeEach
    public void setupTest() {
        postTable = enhancedClient.table("Posts", TableSchema.fromBean(PostEntity.class));
        postTable.createTable(CreateTableEnhancedRequest.builder().build());
    }

    @AfterEach
    public void teardownTest() {
        postTable.deleteTable();
    }

    @Test
    public void testCreateAndGetPost() throws Exception {
        Post postToCreate = Post.builder().title("My First Post").content("Hello, World!").build();

        assertThat(
            this.mockMvcTester.perform(
                post("/api/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postToCreate))
            )
        )
        .hasStatus(HttpStatus.CREATED)
        .bodyJson()
        ;
    
    }
}
