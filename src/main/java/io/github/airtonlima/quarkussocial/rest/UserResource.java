package io.github.airtonlima.quarkussocial.rest;

import io.github.airtonlima.quarkussocial.rest.domain.repository.UserRepository;
import io.github.airtonlima.quarkussocial.rest.dto.CreateUserRequest;
import io.github.airtonlima.quarkussocial.rest.domain.model.User;
import io.github.airtonlima.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private UserRepository repository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response create(CreateUserRequest request) {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            return ResponseError.createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }
        User user = new User();
        user.setName(request.getName());
        user.setAge(request.getAge());
        repository.persist(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    public Response listAll() {
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @GET
    @Path("{id}") // /users/3
    public Response findById(@PathParam("id") Long id) {
        User user = repository.findById(id);
        if (user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("{id}") // /users/3
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        User user = repository.findById(id);
        if (user != null) {
            repository.delete(user);
            return Response.noContent().build(); // HTTP 204
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}") // /users/3
    @Transactional
    public Response update(@PathParam("id") Long id, CreateUserRequest request) {
        User user = repository.findById(id);
        if (user != null) {
            user.setName(request.getName());
            user.setAge(request.getAge());
            // repository.update(user); // Não é necessário por conta da notação @Transactional. O commit será executado assim que o método terminar de ser executado.
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
