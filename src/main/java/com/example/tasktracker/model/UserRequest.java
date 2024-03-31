package com.example.tasktracker.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

	private String username;
	private String email;
	private String password;
	private Set<Role> roles;
}
