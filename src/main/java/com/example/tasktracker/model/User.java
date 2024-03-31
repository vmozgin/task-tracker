package com.example.tasktracker.model;

import java.util.Set;
import lombok.Data;

@Data
public class User {

	private String id;
	private String username;
	private String email;
	private Set<Role> roles;
}
