package com.example.tasktracker.entity;


import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity {

	@Id
	private String id;
	private String username;
	private String email;
	private String password;
	private Set<RoleType> roles = new HashSet<>();
}
