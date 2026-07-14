package com.josephmfaulkner.dualDBDemo.e2eTests;

import com.josephmfaulkner.dualDBDemo.posts.models.Post;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Tag("e2e")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class E2EPostsApiTest extends BaseE2ETest {

    private static String createdPostId;

    @Test
    @org.junit.jupiter.api.Order(1)
    void shouldCreateAndRetrieveNewPost() {
        Post newPost = Post.builder()
                .title("E2E Test Post")
                .content("This is the content of the E2E test post.")
                .build();

        createdPostId = given()
                .contentType(ContentType.JSON)
                .body(newPost)
                .when()
                .post("/api/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("title", equalTo(newPost.getTitle()))
                .body("content", equalTo(newPost.getContent()))
                .extract().path("id");

        given()
                .when()
                .get("/api/posts/{id}", createdPostId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(createdPostId))
                .body("title", equalTo(newPost.getTitle()))
                .body("content", equalTo(newPost.getContent()));
    }

    /* 
    @Test
    @org.junit.jupiter.api.Order(2)
    void shouldDeletePost() {
        given()
                .when()
                .delete("/api/posts/{id}", createdPostId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
    */
}