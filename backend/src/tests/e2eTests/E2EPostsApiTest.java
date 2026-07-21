

import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;

import com.josephmfaulkner.dualDBDemo.posts.dto.Comment;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

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
                .body("title", equalTo(newPost.title()))
                .body("content", equalTo(newPost.content()))
                .extract().path("id");

        given()
                .when()
                .get("/api/posts/{id}", createdPostId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(createdPostId))
                .body("title", equalTo(newPost.title()))
                .body("content", equalTo(newPost.content()));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void shouldCreateAndRetrieveNewPostWithComments() {
        Post newPost = Post.builder()
                .title("E2E Test Post")
                .content("This is the content of the E2E test post.")
                .comments(List.of(
                        Comment.builder().content("Comment 1").build(),
                        Comment.builder().content("Comment 2").build(),
                        Comment.builder().content("Comment 3").build()
                ))
                .build();

        createdPostId = given()
                .contentType(ContentType.JSON)
                .body(newPost)
                .when()
                .post("/api/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("title", equalTo(newPost.title()))
                .body("content", equalTo(newPost.content()))
                .body("comments", hasSize(3))
                .body("comments[0].content", equalTo("Comment 1"))
                .body("comments[1].content", equalTo("Comment 2"))
                .body("comments[2].content", equalTo("Comment 3"))
                .extract().path("id");

        given()
                .when()
                .get("/api/posts/{id}", createdPostId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(createdPostId))
                .body("title", equalTo(newPost.title()))
                .body("content", equalTo(newPost.content()))
                .body("comments", hasSize(3))
                .body("comments[0].content", equalTo("Comment 1"))
                .body("comments[1].content", equalTo("Comment 2"))
                .body("comments[2].content", equalTo("Comment 3"));
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