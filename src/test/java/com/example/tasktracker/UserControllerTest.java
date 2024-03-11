package com.example.tasktracker;

import com.example.tasktracker.entity.UserEntity;
import com.example.tasktracker.mapper.UserMapper;
import com.example.tasktracker.model.User;
import com.example.tasktracker.model.UserRequest;
import com.example.tasktracker.repository.UserRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class UserControllerTest extends AbstractTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserMapper userMapper;

	private UserEntity user1;
	private UserEntity user2;

	@BeforeEach
	public void setup() {
		userRepository.deleteAll().block();
		user1 = new UserEntity("1", "test_user1", "test_email1");
		user2 = new UserEntity("2","test_user2", "test_email2");

		userRepository.saveAll(List.of(user1, user2)).collectList().block();
	}

	@Test
	public void whenCreateUser_thenSaveInDataBase() {
		UserRequest request = new UserRequest("create_username", "create_email");

		webTestClient.post()
				.uri("/api/user")
				.body(Mono.just(request), UserRequest.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(User.class)
				.value(response -> {
					Assertions.assertThat(response.getEmail()).isEqualTo(request.getEmail());
					Assertions.assertThat(response.getUsername()).isEqualTo(request.getUsername());
					Assertions.assertThat(response.getId()).isNotNull();
				});

		StepVerifier.create(userRepository.save(userMapper.userRequestToUserEntity(request)))
				.expectNextCount(1L)
				.verifyComplete();
	}

	@Test
	public void thenGetAll_thenReturnUsers() {
		webTestClient.get()
				.uri("/api/user")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(User.class)
				.hasSize(2)
				.value(users -> {
					Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(
							userMapper.userEntityToUser(user1),
							userMapper.userEntityToUser(user2)
					));
				});
	}

	@Test
	public void whenGetById_thenReturnUser() {
		webTestClient.get()
				.uri("/api/user/{id}", user1.getId())
				.exchange()
				.expectStatus().isOk()
				.expectBody(User.class)
				.isEqualTo(userMapper.userEntityToUser(user1));
	}

	@Test
	public void whenUpdateUser_thenReturnUpdatedItem() {
		UserRequest updateUserRequest = new UserRequest("updated_username", "updated_email");
		webTestClient.put()
				.uri("/api/user/{id}", user1.getId())
				.body(Mono.just(updateUserRequest), UserRequest.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(User.class)
				.value(updatedUser -> {
					Assertions.assertThat(updatedUser.getId()).isEqualTo(user1.getId());
					Assertions.assertThat(updatedUser.getUsername()).isEqualTo(updateUserRequest.getUsername());
					Assertions.assertThat(updatedUser.getEmail()).isEqualTo(updateUserRequest.getEmail());
				});

		StepVerifier.create(userRepository.findById(user1.getId()))
				.expectNextMatches(updatedUser ->
						updatedUser.getUsername().equals(updateUserRequest.getUsername()) &&
						updatedUser.getEmail().equals(updateUserRequest.getEmail())
				)
				.verifyComplete();
	}

	@Test
	public void whenDeleteUser_thenRemoveFromDatabase() {
		webTestClient.delete()
				.uri("/api/user/{id}", user1.getId())
				.exchange()
				.expectStatus().isOk();

		StepVerifier.create(userRepository.count())
				.expectNext(1L)
				.expectComplete()
				.verify();
	}
}
