package com.skronawi.example.pact.producer.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface UserRepository extends CrudRepository<User, String> {

    Collection<User> findAll();
}
