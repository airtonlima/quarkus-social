package io.github.airtonlima.quarkussocial.rest;

import io.github.airtonlima.quarkussocial.rest.domain.model.Follower;
import io.github.airtonlima.quarkussocial.rest.domain.model.User;
import io.github.airtonlima.quarkussocial.rest.domain.repository.FollowerRepository;
import io.github.airtonlima.quarkussocial.rest.domain.repository.UserRepository;
import io.github.airtonlima.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setup() {
        // User
        var user = new User();
        user.setAge(27);
        user.setName("Airton");
        userRepository.persist(user);
        userId = user.getId();
        // Follower 1
        var follower = new User();
        follower.setAge(27);
        follower.setName("Airton 2");
        userRepository.persist(follower);
        followerId = follower.getId();
        // Follower 2
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @Order(1)
    @DisplayName("#01 - Should return 409 when followerId is equal to user id")
    public void sameUserAsFollowerTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
            .when()
                .put()
            .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself."));
    }

    @Test
    @Order(2)
    @DisplayName("#02 - Should return 404 on follow a user when user id doesn't exist.")
    public void userNotFoundWhenTryingToFollowTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 999;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUserId)
            .when()
                .put()
            .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @Order(3)
    @DisplayName("#03 - Should follow a user")
    public void followUserTest() {

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
            .when()
                .put()
            .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(4)
    @DisplayName("#04 - Should return 404 on list user followers and user id doesn't exist.")
    public void userNotFoundWhenListingFollowersTest() {

        var inexistentUserId = 999;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
            .when()
                .get()
            .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @Order(5)
    @DisplayName("#05 - Should list a user followers")
    public void listingFollowersTest() {

        var response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", userId)
                .when()
                    .get()
                .then()
                    .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("followers");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());
    }

    @Test
    @Order(6)
    @DisplayName("#06 - Should return 404 on unfollow user and user id doesn't exist")
    public void userNotFoundWhenUnfollowingAUserTest() {

        var inexistentUserId = 999;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId)
            .when()
                .delete()
            .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @Order(7)
    @DisplayName("#06 - Should unfollow an user")
    public void unfollowUseTest() {
        RestAssured.given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
            .when()
                .delete()
            .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

}