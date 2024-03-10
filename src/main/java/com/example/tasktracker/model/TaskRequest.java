package com.example.tasktracker.model;

import lombok.Data;

@Data
public class TaskRequest {

	private String name;
	private String description;
	private TaskStatus status;
	private String authorId;
	private String assigneeId;
}
