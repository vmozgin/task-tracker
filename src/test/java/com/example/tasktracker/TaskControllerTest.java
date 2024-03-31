package com.example.tasktracker;

import com.example.tasktracker.model.ObserverRequest;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.TaskRequest;
import com.example.tasktracker.model.TaskStatus;
import java.util.Base64;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TaskControllerTest extends AbstractTest{

	@Test
	@WithMockUser(username = "user")
	public void thenGetAll_thenReturnTasks() {
		webTestClient.get()
				.uri("/api/task")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Task.class)
				.hasSize(2)
				.value(users -> Assertions.assertThat(users)
						.containsExactlyInAnyOrderElementsOf(expectedTasks()));
	}

	@Test
	@WithMockUser(username = "user")
	public void whenGetById_thenReturnTask() {
		webTestClient.get()
				.uri("/api/task/{id}", taskEntity2.getId())
				.exchange()
				.expectStatus().isOk()
				.expectBody(Task.class)
				.isEqualTo(expectedTasks().get(1));
	}

	@Test
	public void whenCreateTask_thenSaveInDataBase() {
		TaskRequest request = new TaskRequest(
				"create_name",
				"create_description",
				TaskStatus.IN_PROGRESS,
				"2"
		);

		webTestClient.post()
				.uri("/api/task")
				.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("test_user1:test_user1".getBytes()))
				.body(Mono.just(request), Task.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Task.class)
				.value(response -> {
					Assertions.assertThat(response.getId()).isNotNull();
					Assertions.assertThat(response.getName()).isEqualTo(request.getName());
					Assertions.assertThat(response.getDescription()).isEqualTo(request.getDescription());
					Assertions.assertThat(response.getStatus()).isEqualTo(request.getStatus());
					Assertions.assertThat(response.getAuthorId()).isEqualTo(userEntity1.getId());
					Assertions.assertThat(response.getAssigneeId()).isEqualTo(request.getAssigneeId());
				});

		StepVerifier.create(taskRepository.save(taskMapper.taskRequestToTaskEntity(request, userEntity1.getId())))
				.expectNextCount(1L)
				.verifyComplete();
	}

	@Test
	public void whenUpdateTask_thenReturnUpdatedItem() {
		TaskRequest updateTaskRequest = new TaskRequest(
				"update_name",
				"update_description",
				TaskStatus.IN_PROGRESS,
				"2"
		);

		webTestClient.put()
				.uri("/api/task/{id}", taskEntity1.getId())
				.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("test_user1:test_user1".getBytes()))
				.body(Mono.just(updateTaskRequest), TaskRequest.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Task.class)
				.value(updatedTask -> {
					Assertions.assertThat(updatedTask.getId()).isNotNull();
					Assertions.assertThat(updatedTask.getName()).isEqualTo(updateTaskRequest.getName());
					Assertions.assertThat(updatedTask.getDescription()).isEqualTo(updateTaskRequest.getDescription());
					Assertions.assertThat(updatedTask.getStatus()).isEqualTo(updateTaskRequest.getStatus());
					Assertions.assertThat(updatedTask.getAuthorId()).isEqualTo(userEntity1.getId());
					Assertions.assertThat(updatedTask.getAssigneeId()).isEqualTo(updateTaskRequest.getAssigneeId());
				});

		StepVerifier.create(taskRepository.findById(taskEntity1.getId()))
				.expectNextMatches(updatedTask ->
						updatedTask.getName().equals(updateTaskRequest.getName()) &&
								updatedTask.getDescription().equals(updateTaskRequest.getDescription())
				)
				.verifyComplete();
	}

	@Test
	@WithMockUser(username = "user", roles = {"MANAGER"})
	public void whenDeleteTask_thenRemoveFromDatabase() {
		webTestClient.delete()
				.uri("/api/task/{id}", taskEntity1.getId())
				.exchange()
				.expectStatus().isOk();

		StepVerifier.create(taskRepository.count())
				.expectNext(1L)
				.expectComplete()
				.verify();
	}

	@Test
	@WithMockUser(username = "user")
	public void whenAddObserver_thenSaveInDatabase() {
		ObserverRequest request = new ObserverRequest(userEntity1.getId());

		webTestClient.post()
				.uri("/api/task//observer/add/{taskId}", taskEntity1.getId())
				.body(Mono.just(request), ObserverRequest.class)
				.exchange()
				.expectStatus().isOk();

		StepVerifier.create(taskRepository.findById(taskEntity1.getId()))
				.expectNextMatches(addObserverTask ->
						addObserverTask.getObserverIds().equals(Set.of(request.getObserverId()))
				)
				.verifyComplete();
	}
}
