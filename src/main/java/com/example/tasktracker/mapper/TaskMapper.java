package com.example.tasktracker.mapper;

import com.example.tasktracker.entity.TaskEntity;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.TaskRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
public interface TaskMapper {

	TaskEntity taskRequestToTaskEntity(TaskRequest request, String authorId);

	Task taskEntityToTask(TaskEntity entity);
}
