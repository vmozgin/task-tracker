package com.example.tasktracker.controller;

import com.example.tasktracker.exception.UserNotFoundException;
import com.example.tasktracker.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> authorNotFound(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(ex.getLocalizedMessage()));
	}
}
