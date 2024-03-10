package com.example.tasktracker.entity;

import com.example.tasktracker.model.TaskStatus;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class TaskEntity {

	@Id
	private String id;
	private String name;
	private String description;
	private Instant createdAt;
	private Instant updatedAt;
	private TaskStatus status;
	private String authorId;
	private String assigneeId;
	@Field("observerIds")
	private Set<String> observerIds = new HashSet<>();
	@ReadOnlyProperty
	private UserEntity author;
	@ReadOnlyProperty
	private UserEntity assignee;
	@ReadOnlyProperty
	private Set<UserEntity> observers;
}
