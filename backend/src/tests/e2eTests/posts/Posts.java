package posts;


import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;

import com.josephmfaulkner.dualDBDemo.e2eTests._base.BaseE2ETest;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Tag("e2e")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Posts extends BaseE2ETest {

    private static String postAId;
    private static String postBId;
    private static Post postA;
    private static Post postB;

    @Test
    @org.junit.jupiter.api.Order(1)
    void shouldCreateAndRetrieveNewPosts() {
        Post postToCreateA = Post.builder()
                .title("E2E Test Post A")
                .content("This is the content of the E2E test post A.")
                .build();

        postA = given()
                .contentType(ContentType.JSON)
                .body(postToCreateA)
                .when()
                .post("/api/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("title", equalTo(postToCreateA.title()))
                .body("content", equalTo(postToCreateA.content()))
                .extract().as(Post.class);

        postAId = postA.id();

        given()
                .when()
                .get("/api/posts/{id}",  postAId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(postAId))
                .body("title", equalTo(postA.title()))
                .body("content", equalTo(postA.content()));

        Post postToCreateB = Post.builder()
                .title("E2E Test Post B")
                .content("This is the content of the E2E test post B.")
                .build();

        postB = given()
                .contentType(ContentType.JSON)
                .body(postToCreateB)
                .when()
                .post("/api/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("title", equalTo(postToCreateB.title()))
                .body("content", equalTo(postToCreateB.content()))
                .extract().as(Post.class);

        postBId = postB.id();

        given()
                .when()
                .get("/api/posts/{id}",  postBId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(postBId))
                .body("title", equalTo(postB.title()))
                .body("content", equalTo(postB.content()));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void shouldGetAllPosts() {
        given()
                .when()
                .get("/api/posts")
                .then()
                .statusCode(HttpStatus.OK.value())
                //.body("$", hasSize(3))
                .body("id", hasItems(postA.id(), postB.id()))
                .body("title", hasItems(postA.title(), postB.title()))
                .body("content", hasItems(postA.content(), postB.content()))
                ;
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void shouldUpdatePost() {
        Post postAUpdated = Post.builder()
                .id(postAId)
                .title("E2E Test Post A UPDATED Title")
                .content("This is the UPDATED content of the E2E test post A.")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(postAUpdated)
                .when()
                .put("/api/posts/{id}", postAUpdated.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(postAUpdated.id()))
                .body("title", equalTo(postAUpdated.title()))
                .body("content", equalTo(postAUpdated.content()));

        given()
                .when()
                .get("/api/posts/{id}",  postAUpdated.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(postAUpdated.id()))
                .body("title", equalTo(postAUpdated.title()))
                .body("content", equalTo(postAUpdated.content()));

        postA = postAUpdated;
    }




    @Test
    @org.junit.jupiter.api.Order(4)
    void shouldDeletePosts() {
        given()
                .when()
                .delete("/api/posts/{id}",  postAId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .when()
                .delete("/api/posts/{id}",  postBId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    
}