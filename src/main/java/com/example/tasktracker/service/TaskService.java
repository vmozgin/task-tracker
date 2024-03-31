package com.example.tasktracker.service;

import com.example.tasktracker.entity.TaskEntity;
import com.example.tasktracker.exception.UserNotFoundException;
import com.example.tasktracker.model.TaskRequest;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import java.time.Instant;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;
	private final UserRepository userRepository;

	public Mono<TaskEntity> create(TaskEntity entity) {
		entity.setCreatedAt(Instant.now());
		entity.setUpdatedAt(Instant.now());

		return userRepository.findById(entity.getAuthorId())
				.switchIfEmpty(Mono.error(new UserNotFoundException(String.format("Пользователь с authorId = %s не найден", entity.getAuthorId()))))
				.flatMap(assignee -> userRepository.findById(entity.getAssigneeId())
						.switchIfEmpty(Mono.error(new UserNotFoundException(String.format("Пользователь с assigneeId = %s не найден", entity.getAssigneeId()))))
				.flatMap(user -> taskRepository.save(entity)));
	}

	public Flux<TaskEntity> findAll() {
		Flux<TaskEntity> tasksFlux = taskRepository.findAll();

		return Flux.zip(tasksFlux,
						tasksFlux.flatMapSequential(task -> userRepository.findById(task.getAuthorId())),
						tasksFlux.flatMapSequential(task -> userRepository.findById(task.getAssigneeId())),
						tasksFlux.flatMapSequential(task ->
								Flux.fromIterable(task.getObserverIds())
										.flatMapSequential(userRepository::findById)
										.collectList()
										.map(HashSet::new)
						))
				.map(tuple -> {
					TaskEntity task = tuple.getT1();
					task.setAuthor(tuple.getT2());
					task.setAssignee(tuple.getT3());
					task.setObservers(tuple.getT4());
					return task;
				});
	}

	public Mono<TaskEntity> findById(String id) {
		Mono<TaskEntity> taskFLux = taskRepository.findById(id);

		return Mono.zip(taskFLux,
				taskFLux.flatMap(task -> userRepository.findById(task.getAuthorId())),
				taskFLux.flatMap(task -> userRepository.findById(task.getAssigneeId())),
				taskFLux.flatMap(task -> Flux.fromIterable(task.getObserverIds())
						.flatMap(userRepository::findById)
						.collectList()
						.map(HashSet::new)
				))
				.map(tuple -> {
					TaskEntity task = tuple.getT1();
					task.setAuthor(tuple.getT2());
					task.setAssignee(tuple.getT3());
					task.setObservers(tuple.getT4());
					return task;
				});
	}

	public Mono<Void> addObserver(String taskId, String observerId) {
		return userRepository.findById(observerId)
				.switchIfEmpty(Mono.error(new UserNotFoundException(String.format("Пользователь с observerId = %s не найден", observerId))))
				.flatMap(task -> findById(taskId))
				.map(task -> {
					task.getObserverIds().add(observerId);
					task.setUpdatedAt(Instant.now());
					return task;
				})
				.flatMap(taskRepository::save)
				.then();
	}

	public Mono<TaskEntity> update(String taskId, TaskRequest request, String authorId) {
		return userRepository.findById(authorId)
				.switchIfEmpty(Mono.error(new UserNotFoundException(String.format("Пользователь с authorId = %s не найден", authorId))))
				.flatMap(assignee -> userRepository.findById(request.getAssigneeId())
						.switchIfEmpty(Mono.error(new UserNotFoundException(String.format("Пользователь с assigneeId = %s не найден", request.getAssigneeId()))))
						.flatMap(oldTask -> taskRepository.findById(taskId)
								.flatMap(taskForUpdate -> {
									taskForUpdate.setName(request.getName());
									taskForUpdate.setAssigneeId(request.getAssigneeId());
									taskForUpdate.setAuthorId(authorId);
									taskForUpdate.setDescription(request.getDescription());
									taskForUpdate.setStatus(request.getStatus());
									taskForUpdate.setUpdatedAt(Instant.now());
									return taskRepository.save(taskForUpdate)
											.then(findById(taskId));
								}))
				);
	}

	public Mono<Void> delete(String taskId) {
		return taskRepository.deleteById(taskId);
	}
}
