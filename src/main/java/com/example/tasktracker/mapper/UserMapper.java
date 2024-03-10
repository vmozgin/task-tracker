package com.example.tasktracker.mapper;

import com.example.tasktracker.entity.UserEntity;
import com.example.tasktracker.model.User;
import com.example.tasktracker.model.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

	User userEntityToUser(UserEntity userEntity);

	UserEntity userRequestToUserEntity(UserRequest userRequest);
}
