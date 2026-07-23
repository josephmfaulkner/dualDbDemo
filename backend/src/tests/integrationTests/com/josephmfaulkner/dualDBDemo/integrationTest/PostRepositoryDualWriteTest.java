package com.josephmfaulkner.dualDBDemo.integrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.core.task.TaskExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephmfaulkner.dualDBDemo.integrationTest.config.DualWriteDbTestConfiguration;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;
import com.josephmfaulkner.dualDBDemo.posts.persistence.PostRepository;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dual.PostRepositoryDualWrite;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.PostRepositoryDynamoDb;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.models.PostEntity;
import com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.PostJpaRepository;
import com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.PostRepositoryPostgres;
import com.josephmfaulkner.dualDBDemo.testFixtures._BaseDualDbTest;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Import(DualWriteDbTestConfiguration.class)
public class PostRepositoryDualWriteTest extends _BaseDualDbTest {

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<PostEntity> postTable;


    private PostRepositoryDynamoDb postRepositoryDynamoDb;
    private PostRepositoryPostgres postRepositoryPosgres;
    private PostRepositoryDualWrite postRepositoryDualWrite;

    @BeforeAll
    public void init() {

        TaskExecutor syncExecutor = new SyncTaskExecutor();
        
        postTable = enhancedClient.table("Posts", TableSchema.fromBean(PostEntity.class));
        postTable.createTable(CreateTableEnhancedRequest.builder().build());
        postRepositoryDynamoDb = new PostRepositoryDynamoDb(enhancedClient);

        postRepositoryPosgres = new PostRepositoryPostgres(postJpaRepository);

        postRepositoryDualWrite = new PostRepositoryDualWrite(postRepositoryDynamoDb, postRepositoryPosgres, syncExecutor);

    }

    @AfterAll
    public void teardownTest() {
        postTable.deleteTable();
    }

    @Test 
    public void shouldPersistNewPost() {
        Post newPost = Post.builder()
            .title("Test new post Title")
            .content("Test new post Content")
        .build();

        Post savedPost = postRepositoryDualWrite.savePost(newPost);
        assertNotNull(savedPost.id());

        Post retrievedPostDualDatabase = postRepositoryDualWrite.getPostById(savedPost.id());
        assertNotNull(retrievedPostDualDatabase);

        Post retrievedPostDynamoDb = postRepositoryDynamoDb.getPostById(savedPost.id());
        assertNotNull(retrievedPostDualDatabase);

        Post retrievedPostPosgres = postRepositoryPosgres.getPostById(savedPost.id());
        assertNotNull(retrievedPostDualDatabase);

        assertEquals(retrievedPostDualDatabase, retrievedPostDynamoDb);
        assertEquals(retrievedPostDualDatabase, retrievedPostPosgres);
        
    }







    


}
