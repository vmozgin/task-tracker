package com.example.tasktracker;

import com.example.tasktracker.entity.RoleType;
import com.example.tasktracker.entity.TaskEntity;
import com.example.tasktracker.entity.UserEntity;
import com.example.tasktracker.mapper.TaskMapper;
import com.example.tasktracker.mapper.UserMapper;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.TaskStatus;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureWebTestClient
public class AbstractTest {

	static MongoDBContainer mongoDBContainer;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Autowired
	protected WebTestClient webTestClient;
	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected TaskRepository taskRepository;
	@Autowired
	protected TaskMapper taskMapper;
	@Autowired
	protected UserMapper userMapper;

	protected UserEntity userEntity1 = new UserEntity(
			"1",
			"test_user1",
			"test_email1",
			"test_user1",
			Set.of(RoleType.ROLE_USER, RoleType.ROLE_MANAGER));
	protected UserEntity userEntity2 = new UserEntity(
			"2",
			"test_user2",
			"test_email2",
			"test_user2",
			Set.of(RoleType.ROLE_USER, RoleType.ROLE_MANAGER));

	protected TaskEntity taskEntity1;
	protected TaskEntity taskEntity2;

	@BeforeAll
	static void setUpContainer() {
		mongoDBContainer = new MongoDBContainer("mongo:6.0.8")
				.withReuse(true);
		mongoDBContainer.start();
	}

	@BeforeEach
	public void setup() {
		userRepository.deleteAll().block();
		userRepository.saveAll(List.of(userEntity1, userEntity2)).collectList().block();
		taskRepository.deleteAll().block();
		taskRepository.saveAll(setTaskEntities()).collectList().block();
	}

	private List<TaskEntity> setTaskEntities() {
		taskEntity1 = new TaskEntity();
		taskEntity1.setId("1");
		taskEntity1.setName("task1");
		taskEntity1.setAuthorId(userEntity1.getId());
		taskEntity1.setStatus(TaskStatus.TODO);
		taskEntity1.setDescription("description1");
		taskEntity1.setCreatedAt(Instant.now().with(ChronoField.NANO_OF_SECOND, 0));
		taskEntity1.setUpdatedAt(Instant.now().with(ChronoField.NANO_OF_SECOND, 0));
		taskEntity1.setAssigneeId(userEntity2.getId());
		taskEntity1.setObservers(new HashSet<>());

		taskEntity2 = new TaskEntity();
		taskEntity2.setId("2");
		taskEntity2.setName("task2");
		taskEntity2.setAuthorId(userEntity2.getId());
		taskEntity2.setStatus(TaskStatus.DONE);
		taskEntity2.setDescription("description2");
		taskEntity2.setCreatedAt(Instant.now().with(ChronoField.NANO_OF_SECOND, 0));
		taskEntity2.setUpdatedAt(Instant.now().with(ChronoField.NANO_OF_SECOND, 0));
		taskEntity2.setAssigneeId(userEntity1.getId());
		taskEntity2.setObserverIds(Set.of(userEntity1.getId(), userEntity2.getId()));

		return List.of(taskEntity1, taskEntity2);
	}

	protected List<Task> expectedTasks() {
		User user1 = userMapper.userEntityToUser(userEntity1);
		User user2 = userMapper.userEntityToUser(userEntity2);

		Task task1 = taskMapper.taskEntityToTask(taskEntity1);
		task1.setAuthor(user1);
		task1.setAssignee(user2);

		Task task2 = taskMapper.taskEntityToTask(taskEntity2);
		task2.setAssignee(user1);
		task2.setAuthor(user2);
		task2.setObservers(Set.of(user1, user2));

		return List.of(task1, task2);
	}
}
