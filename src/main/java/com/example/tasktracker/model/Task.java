package com.example.tasktracker.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class Task {

	private String id;
	private String name;
	private String description;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Instant createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Instant updatedAt;
	private TaskStatus status;
	private String authorId;
	private String assigneeId;
	private Set<String> observerIds = new HashSet<>();
	private User author;
	private User assignee;
	private Set<User> observers;
}
