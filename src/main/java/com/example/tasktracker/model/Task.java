package com.example.tasktracker.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class Task {

	private String id;
	private String name;
	private String description;
	private Instant createdAt;
	private Instant updatedAt;
	private TaskStatus status;
	private String authorId;
	private String assigneeId;
	private Set<String> observerIds = new HashSet<>();
	private User author;
	private User assignee;
	private Set<User> observers;
}
