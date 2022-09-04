package io.github.airtonlima.quarkussocial.rest;

import io.github.airtonlima.quarkussocial.rest.domain.model.Follower;
import io.github.airtonlima.quarkussocial.rest.domain.model.Post;
import io.github.airtonlima.quarkussocial.rest.domain.model.User;
import io.github.airtonlima.quarkussocial.rest.domain.repository.FollowerRepository;
import io.github.airtonlima.quarkussocial.rest.domain.repository.PostRepository;
import io.github.airtonlima.quarkussocial.rest.domain.repository.UserRepository;
import io.github.airtonlima.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostResourceTest {
    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setup() {
        // User default
        var userDefault = new User();
        userDefault.setAge(27);
        userDefault.setName("User Default Name");
        userRepository.persist(userDefault);
        userId = userDefault.getId();
        // User not follower
        var userNotFollower = new User();
        userNotFollower.setAge(26);
        userNotFollower.setName("User Not Follower Name");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();
        // User Follower
        var userFollower = new User();
        userFollower.setAge(26);
        userFollower.setName("User 3 Name");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();
        // Post's Default User
        Post post = new Post();
        post.setText("Hello");
        post.setUser(userDefault);
        postRepository.persist(post);
        // Follower
        Follower follower = new Follower();
        follower.setUser(userDefault);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("#01 - Should create a post for a user")
    @Order(1)
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(postRequest)
                    .pathParam("userId", userId)
                .when()
                    .post()
                .then()
                    .statusCode(201);
    }

    @Test
    @DisplayName("#02 - Should return 404 when trying to make a post for an inexistent user")
    @Order(2)
    public void postForAnInexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some Text");

        var inexistentUserId = 99;

        RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUserId)
            .when()
                .post()
            .then()
                .statusCode(404);
    }

    @Test
    @Order(3)
    @DisplayName("#03 - Should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest() {
        var inexistentUserId = 999;
        RestAssured
                .given()
                    .pathParam("userId", inexistentUserId)
                .when()
                    .get()
                .then()
                    .statusCode(404);

    }

    @Test
    @Order(4)
    @DisplayName("#04 - Should return 400 when followerId Header is not present")
    public void listPostFollowerHeaderNotSendTest() {
        RestAssured
                .given()
                    .pathParam("userId", userId)
                .when()
                    .get()
                .then()
                    .statusCode(400)
                    .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @Order(5)
    @DisplayName("#05 - Should return 400 when follower doesn`t exist")
    public void listPostFollowerNotFoundTest() {
        var inexistentFollower = 999;
        RestAssured
                .given()
                    .pathParam("userId", userId)
                    .header("followerId", inexistentFollower)
                .when()
                    .get()
                .then()
                    .statusCode(400)
                    .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @Order(6)
    @DisplayName("#06 - Should return 403 when follower isn't a follower")
    public void listPostNotAFollower() {
        RestAssured
                .given()
                    .pathParam("userId", userId)
                    .header("followerId", userNotFollowerId)
                .when()
                    .get()
                .then()
                    .statusCode(403)
                    .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @Order(7)
    @DisplayName("#07 - Should list posts")
    public void listPostsTest() {
        RestAssured
                .given()
                    .pathParam("userId", userId)
                    .header("followerId", userFollowerId)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .body("size()", Matchers.is(1));
    }
}