package io.github.airtonlima.quarkussocial.rest;

import io.github.airtonlima.quarkussocial.rest.domain.model.Post;
import io.github.airtonlima.quarkussocial.rest.domain.repository.FollowerRepository;
import io.github.airtonlima.quarkussocial.rest.domain.repository.PostRepository;
import io.github.airtonlima.quarkussocial.rest.domain.repository.UserRepository;
import io.github.airtonlima.quarkussocial.rest.dto.CreatePostRequest;
import io.github.airtonlima.quarkussocial.rest.dto.PostResponse;
import io.github.airtonlima.quarkussocial.rest.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository,
                        PostRepository postRepository,
                        FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response create(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);
        postRepository.persist(post);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listAll(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId) {

        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        if (followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId").build();
        }
        User follower = userRepository.findById(followerId);
        if (follower == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Inexistent followerId").build();
        }
        boolean follows = followerRepository.follows(follower, user);
        if (!follows) {
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts").build();
        }
        // repository.find("SELECT * FROM public.posts where user_id = : user");
        PanacheQuery<Post> query = postRepository.find("user",
                Sort.by("createdAt", Sort.Direction.Descending), user);
        List<PostResponse> postResponseList = query.list().stream()
            .map(PostResponse::fromEntity) // .map(post -> PostResponse.fromEntity(post))
            .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }
}
