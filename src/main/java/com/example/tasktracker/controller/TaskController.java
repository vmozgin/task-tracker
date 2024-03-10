package com.example.tasktracker.controller;

import com.example.tasktracker.mapper.TaskMapper;
import com.example.tasktracker.model.ObserverRequest;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.TaskRequest;
import com.example.tasktracker.service.TaskService;
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
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;
	private final TaskMapper taskMapper;

	@PostMapping
	public Mono<ResponseEntity<Task>> create(@RequestBody TaskRequest request) {
		return taskService.create(taskMapper.taskRequestToTaskEntity(request))
				.map(taskMapper::taskEntityToTask)
				.map(ResponseEntity::ok);
	}

	@GetMapping
	public Flux<Task> findAll() {
		return taskService.findAll().map(taskMapper::taskEntityToTask);
	}

	@GetMapping("{id}")
	public Mono<ResponseEntity<Task>> findById(@PathVariable String id) {
		return taskService.findById(id)
				.map(taskMapper::taskEntityToTask)
				.map(ResponseEntity::ok);
	}

	@PostMapping("/observer/add/{taskId}")
	public Mono<ResponseEntity<Void>> addObserver(@PathVariable String taskId, @RequestBody ObserverRequest request) {
		return taskService.addObserver(taskId, request.getObserverId())
				.map(ResponseEntity::ok);
	}

	@PutMapping("{id}")
	public Mono<ResponseEntity<Task>> updateById(@PathVariable("id") String taskId, @RequestBody TaskRequest request) {
		return taskService.update(taskId, request)
				.map(taskMapper::taskEntityToTask)
				.map(ResponseEntity::ok);
	}

	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Void>> deleteById(@PathVariable("id") String taskId) {
		return taskService.delete(taskId)
				.map(ResponseEntity::ok);
	}
}
