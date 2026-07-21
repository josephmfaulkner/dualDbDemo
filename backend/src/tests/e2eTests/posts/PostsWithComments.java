package posts;


import com.josephmfaulkner.dualDBDemo.e2eTests._base.BaseE2ETest;
import com.josephmfaulkner.dualDBDemo.posts.dto.Comment;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;

import io.restassured.http.ContentType;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("e2e")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostsWithComments extends BaseE2ETest {

    private static Post existingPost;

    @BeforeAll
    static void setUp() {
        // Create a post with some comments to be used in the tests
        Post newPost = Post.builder()
                .title("Original Title")
                .content("Original Content")
                .comments(List.of(

                ))
                .build();

        existingPost = given()
                .contentType(ContentType.JSON)
                .body(newPost)
                .when()
                .post("/api/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(Post.class);
    }

    @AfterAll
    void tearDown() {
        given()
                .when()
                .delete("/api/posts/{id}", existingPost.id())
                .then()
                .statusCode(anyOf(is(HttpStatus.NO_CONTENT.value()), is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    @Order(1)
    void shouldUpdatePostTitleAndContent() {
        Post updatedPostData = Post.builder()
                .id(existingPost.id())
                .title("Updated Title")
                .content("Updated Content")
                .comments(existingPost.comments()) // Keep original comments
                .build();

        existingPost = given()
                .contentType(ContentType.JSON)
                .body(updatedPostData)
                .when()
                .put("/api/posts/{id}", existingPost.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(existingPost.id()))
                .body("title", equalTo("Updated Title"))
                .body("content", equalTo("Updated Content"))
                .body("comments", hasSize(0))
                .extract().as(Post.class);
    }

    @Test
    @Order(2)
    void shouldAddCommentToPost() {
        // Now, update it to add comments
        List<Comment> newComments = List.of(Comment.builder().content("A new comment").build());
        Post postUpdateData = Post.builder()
                .id(existingPost.id())
                .title(existingPost.title())
                .content(existingPost.content())
                .comments(newComments)
                .build();

        existingPost = given()
                .contentType(ContentType.JSON)
                .body(postUpdateData)
                .when()
                .put("/api/posts/{id}", postUpdateData.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(postUpdateData.id()))
                .body("comments", hasSize(1))
                .body("comments[0].content", equalTo("A new comment"))
                .extract().as(Post.class);
        
        assertEquals(existingPost.comments().size(),1);

    }



    @Test
    @Order(3)
    void shouldUpdateExistingComment() {

        assertEquals(existingPost.comments().size(),1);

        Comment commentToUpdate = existingPost.comments().get(0);
        Comment updatedComment = new Comment(commentToUpdate.id(), "Updated comment content");

        List<Comment> updatedCommentsList = new ArrayList<>(existingPost.comments());
        updatedCommentsList.set(0, updatedComment);

        Post postUpdateData = Post.builder()
                .id(existingPost.id())
                .title(existingPost.title())
                .content(existingPost.content())
                .comments(updatedCommentsList)
                .build();

        existingPost = given()
                .contentType(ContentType.JSON)
                .body(postUpdateData)
                .when()
                .put("/api/posts/{id}", existingPost.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(existingPost.id()))
                .body("comments", hasSize(1))
                .body("comments[0].id", equalTo(commentToUpdate.id()))
                .body("comments[0].content", equalTo("Updated comment content"))
                .extract().as(Post.class);

    }

    @Test
    @Order(4)
    void shouldAddNewCommentToExistingComments() {
        Comment newComment = Comment.builder().content("A brand new comment").build();

        List<Comment> updatedCommentsList = new ArrayList<>(existingPost.comments());
        updatedCommentsList.add(newComment);

        Post postUpdateData = Post.builder()
                .id(existingPost.id())
                .title(existingPost.title())
                .content(existingPost.content())
                .comments(updatedCommentsList)
                .build();

        existingPost = given()
                .contentType(ContentType.JSON)
                .body(postUpdateData)
                .when()
                .put("/api/posts/{id}", existingPost.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(existingPost.id()))
                .body("comments", hasSize(2))
                .body("comments[1].content", equalTo("A brand new comment"))
                .body("comments[1].id", notNullValue())
                .extract().as(Post.class);

        assertEquals(existingPost.comments().size(),2);


    }

    @Test
    @Order(5)
    void shouldRemoveOneCommentFromExistingComments() {
        assertEquals(existingPost.comments().size(),2);

        List<Comment> updatedCommentsList = new ArrayList<>(existingPost.comments());
        updatedCommentsList.remove(0);

        Post postUpdateData = Post.builder()
                .id(existingPost.id())
                .title(existingPost.title())
                .content(existingPost.content())
                .comments(updatedCommentsList)
                .build();

        existingPost = given()
                .contentType(ContentType.JSON)
                .body(postUpdateData)
                .when()
                .put("/api/posts/{id}", existingPost.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(existingPost.id()))
                .body("comments", hasSize(1))
                .body("comments[0].id", equalTo(existingPost.comments().get(1).id()))
                .extract().as(Post.class);

    }

    @Test
    @Order(6)
    void shouldRemoveAllCommentsFromPost() {
        Post postUpdateDataWithNull = Post.builder()
                .id(existingPost.id())
                .title(existingPost.title())
                .content(existingPost.content())
                .comments(List.of())
                .build();

        existingPost = given()
                .contentType(ContentType.JSON)
                .body(postUpdateDataWithNull)
                .when()
                .put("/api/posts/{id}", existingPost.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(existingPost.id()))
                .body("comments", emptyIterable())
                .extract().as(Post.class);

    }

    @Test
    @Order(7)
    void shouldReturnNotFoundWhenUpdatingNonExistentPost() {
        String nonExistentId = UUID.randomUUID().toString();
        Post postUpdateData = Post.builder().title("title").content("content").build();

        given()
                .contentType(ContentType.JSON)
                .body(postUpdateData)
                .when()
                .put("/api/posts/{id}", nonExistentId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    
}