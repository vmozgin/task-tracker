package com.example.tasktracker.service;

import com.example.tasktracker.entity.UserEntity;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public Flux<UserEntity> findAll() {
		return userRepository.findAll();
	}

	public Mono<UserEntity> create(UserEntity entity) {
		return userRepository.save(entity);
	}

	public Mono<UserEntity> findById(String id) {
		return userRepository.findById(id);
	}

	public Mono<UserEntity> update(String id, UserEntity entity) {
		return findById(id)
				.flatMap(entityForUpdate -> {
					entityForUpdate.setEmail(entity.getEmail());
					entityForUpdate.setUsername(entity.getUsername());

					return userRepository.save(entityForUpdate);
				});
	}

	public Mono<Void> deleteById(String id) {
		return userRepository.deleteById(id);
	}
}
