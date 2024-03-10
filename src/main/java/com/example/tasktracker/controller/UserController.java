package com.example.tasktracker.controller;

import com.example.tasktracker.mapper.UserMapper;
import com.example.tasktracker.model.User;
import com.example.tasktracker.model.UserRequest;
import com.example.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;

	@GetMapping
	public Flux<User> findAll() {
		return userService.findAll()
				.map(userMapper::userEntityToUser);
	}

	@PostMapping
	public Mono<ResponseEntity<User>> create(@RequestBody UserRequest request) {
		return userService.create(userMapper.userRequestToUserEntity(request))
				.map(userMapper::userEntityToUser)
				.map(ResponseEntity::ok);
	}

	@GetMapping("{id}")
	public Mono<ResponseEntity<User>> findById(@PathVariable String id) {
		return userService.findById(id)
				.map(userMapper::userEntityToUser)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping("{id}")
	public Mono<ResponseEntity<User>> update(
			@PathVariable String id,
			@RequestBody UserRequest request
	) {
		return userService.update(id, userMapper.userRequestToUserEntity(request))
				.map(userMapper::userEntityToUser)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping("{id}")
	public Mono<Void> delete(@PathVariable String id) {
		return userService.deleteById(id);
	}
}
