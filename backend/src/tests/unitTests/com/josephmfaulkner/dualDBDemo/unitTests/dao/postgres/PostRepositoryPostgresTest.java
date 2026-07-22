package com.josephmfaulkner.dualDBDemo.unitTests.dao.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.josephmfaulkner.dualDBDemo.posts.dto.Comment;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;
import com.josephmfaulkner.dualDBDemo.posts.persistence.PostRepository;
import com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.PostJpaRepository;
import com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.PostRepositoryPostgres;
import com.josephmfaulkner.dualDBDemo.testFixtures._BaseLocalPostgresDbTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostRepositoryPostgresTest extends _BaseLocalPostgresDbTest {

    
    @Autowired
    private PostJpaRepository postJpaRepository;

    private PostRepositoryPostgres postRepository;

    @BeforeAll
    public void init() {
        postRepository = new PostRepositoryPostgres(postJpaRepository);
    }


    @Test
    void shouldSaveAndQueryPost() {
        Post post = Post.builder()
            .id("test-id-a")
            .title("Test new post Title")
            .content("Test new post Content")
        .build();

        Post savedPost = postRepository.savePost(post);
        assertNotNull(savedPost.id());

        Post retrievedPost = postRepository.getPostById(savedPost.id());
        assertNotNull(retrievedPost);
        assertEquals(retrievedPost.id(), savedPost.id());
        assertEquals(retrievedPost, savedPost);

    }

    @Test 
    void shouldSaveAndQueryPostWithComments() {
        Post post = Post.builder()
            .id("test-id-b")
            .title("Test new post Title")
            .content("Test new post Content")
            .comments(
                List.of(
                    Comment.builder()
                        .content("Test Comment A")
                    .build(),
                    Comment.builder()
                        .content("Test Comment B")
                    .build()
                )
            )
        .build();

        Post savedPost = postRepository.savePost(post);
        assertNotNull(savedPost.id());

        Post retrievedPost = postRepository.getPostById(savedPost.id());
        assertNotNull(retrievedPost);
        assertEquals(retrievedPost.id(), savedPost.id());
        assertEquals(retrievedPost, savedPost);

        assertEquals(2, retrievedPost.comments().size());
        assertEquals(retrievedPost.comments(), savedPost.comments());

    }


}
