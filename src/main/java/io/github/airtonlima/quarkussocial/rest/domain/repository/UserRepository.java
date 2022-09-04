package io.github.airtonlima.quarkussocial.rest.domain.repository;

import io.github.airtonlima.quarkussocial.rest.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> { }
