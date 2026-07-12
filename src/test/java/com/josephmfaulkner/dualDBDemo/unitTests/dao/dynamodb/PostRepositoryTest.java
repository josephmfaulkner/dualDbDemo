package com.josephmfaulkner.dualDBDemo.unitTests.dao.dynamodb;

import com.josephmfaulkner.dualDBDemo._BaseLocalDynamoDbTest;
import com.josephmfaulkner.dualDBDemo.exceptions.PostNotFoundException;
import com.josephmfaulkner.dualDBDemo.posts.PostRepository;
import com.josephmfaulkner.dualDBDemo.posts.models.Comment;
import com.josephmfaulkner.dualDBDemo.posts.models.Post;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class PostRepositoryTest extends _BaseLocalDynamoDbTest {

    private PostRepository postRepository;
    private DynamoDbTable<Post> postTable;

    @BeforeEach
    public void setupTest() {
        postRepository = new PostRepository(enhancedClient);

        postTable = enhancedClient.table("Posts", TableSchema.fromBean(Post.class));
        postTable.createTable(CreateTableEnhancedRequest.builder().build());
    }

    @AfterEach
    public void teardownTest() {
        postTable.deleteTable();
    }

    @Test
    public void testSaveAndGetPostById() {
        Post post = Post.builder()
                .title("Test Title")
                .content("Test Content")
                .comments(Collections.singletonList(Comment.builder().id(1).content("A comment").build()))
                .build();
        Post savedPost = postRepository.savePost(post);

        assertNotNull(savedPost.getId());

        Post retrievedPost = postRepository.getPostById(savedPost.getId());
        assertNotNull(retrievedPost);
        assertEquals(savedPost.getId(), retrievedPost.getId());
        assertEquals(post.getTitle(), retrievedPost.getTitle());
        assertEquals(post.getContent(), retrievedPost.getContent());
    }

    @Test
    public void testGetAllPosts() {
        Post post1 = Post.builder().title("Title 1").content("Content 1").build();
        Post post2 = Post.builder().title("Title 2").content("Content 2").build();
        postRepository.savePost(post1);
        postRepository.savePost(post2);

        List<Post> posts = postRepository.getAllPosts();
        assertEquals(2, posts.size());
    }

    @Test
    public void testUpdatePost() {
        Post post = Post.builder().title("Original Title").content("Original Content").build();
        Post savedPost = postRepository.savePost(post);

        savedPost.setTitle("Updated Title");
        savedPost.setContent("Updated Content");
        Post updatedPost = postRepository.updatePost(savedPost);

        assertNotNull(updatedPost);
        assertEquals("Updated Title", updatedPost.getTitle());
        assertEquals("Updated Content", updatedPost.getContent());

        Post retrievedPost = postRepository.getPostById(savedPost.getId());
        assertEquals("Updated Title", retrievedPost.getTitle());
    }

    @Test
    public void testDeletePost() {
        Post post = Post.builder().title("To Be Deleted").content("Some content").build();
        Post savedPost = postRepository.savePost(post);

        // The post exists, so no exception should be thrown
        assertDoesNotThrow(() -> postRepository.deletePost(savedPost.getId()));

        // Now that it's deleted, trying to get it should throw an exception
        assertThrows(PostNotFoundException.class, () -> postRepository.getPostById(savedPost.getId()));
    }

    @Test
    public void testGetPostById_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        assertThrows(PostNotFoundException.class, () -> postRepository.getPostById(nonExistentId));
    }

    @Test
    public void testUpdatePost_NotFound() {
        Post post = Post.builder()
                .id(UUID.randomUUID().toString())
                .title("Non-existent post")
                .content("This post does not exist in the DB")
                .build();
        assertThrows(PostNotFoundException.class, () -> postRepository.updatePost(post));
    }

    @Test
    public void testDeletePost_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        assertThrows(PostNotFoundException.class, () -> postRepository.deletePost(nonExistentId));
    }
    
}
