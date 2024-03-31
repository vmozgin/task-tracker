package com.example.tasktracker;

import com.example.tasktracker.model.Role;
import com.example.tasktracker.model.User;
import com.example.tasktracker.model.UserRequest;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UserControllerTest extends AbstractTest {

	@Test
	public void whenCreateUser_thenSaveInDataBase() {
		UserRequest request = new UserRequest(
				"create_username",
				"create_email",
				"create_username",
				Set.of(Role.ROLE_USER));

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
					Assertions.assertThat(response.getRoles()).isEqualTo(request.getRoles());
				});

		StepVerifier.create(userRepository.save(userMapper.userRequestToUserEntity(request)))
				.expectNextCount(1L)
				.verifyComplete();
	}

	@Test
	@WithMockUser(username = "user")
	public void thenGetAll_thenReturnUsers() {
		webTestClient.get()
				.uri("/api/user")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(User.class)
				.hasSize(2)
				.value(users -> {
					Assertions.assertThat(users).containsExactlyInAnyOrderElementsOf(List.of(
							userMapper.userEntityToUser(userEntity1),
							userMapper.userEntityToUser(userEntity2)
					));
				});
	}

	@Test
	@WithMockUser(username = "user")
	public void whenGetById_thenReturnUser() {
		webTestClient.get()
				.uri("/api/user/{id}", userEntity1.getId())
				.exchange()
				.expectStatus().isOk()
				.expectBody(User.class)
				.isEqualTo(userMapper.userEntityToUser(userEntity1));
	}

	@Test
	@WithMockUser(username = "user")
	public void whenUpdateUser_thenReturnUpdatedItem() {
		UserRequest updateUserRequest = new UserRequest(
				"updated_username",
				"updated_email",
				"updated_username",
				Set.of(Role.ROLE_USER));

		webTestClient.put()
				.uri("/api/user/{id}", userEntity1.getId())
				.body(Mono.just(updateUserRequest), UserRequest.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(User.class)
				.value(updatedUser -> {
					Assertions.assertThat(updatedUser.getId()).isEqualTo(userEntity1.getId());
					Assertions.assertThat(updatedUser.getUsername()).isEqualTo(updateUserRequest.getUsername());
					Assertions.assertThat(updatedUser.getEmail()).isEqualTo(updateUserRequest.getEmail());
				});

		StepVerifier.create(userRepository.findById(userEntity1.getId()))
				.expectNextMatches(updatedUser ->
						updatedUser.getUsername().equals(updateUserRequest.getUsername()) &&
						updatedUser.getEmail().equals(updateUserRequest.getEmail())
				)
				.verifyComplete();
	}

	@Test
	@WithMockUser(username = "user")
	public void whenDeleteUser_thenRemoveFromDatabase() {
		webTestClient.delete()
				.uri("/api/user/{id}", userEntity1.getId())
				.exchange()
				.expectStatus().isOk();

		StepVerifier.create(userRepository.count())
				.expectNext(1L)
				.expectComplete()
				.verify();
	}
}
