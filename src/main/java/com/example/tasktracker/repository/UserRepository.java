package com.example.tasktracker.repository;

import com.example.tasktracker.entity.UserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<UserEntity, String> {
}
