package br.com.base.mappers;

import br.com.base.domain.Task;
import br.com.base.records.TaskResponse;

public class TaskMapper {
	
	private TaskMapper() {}
	
	public static TaskResponse toResponse(Task task) {
		
		if (task == null) {
			return null;
		}
		
		var responsible = task.getResponsibleUser() != null
				? UserMapper.toUserResponse(task.getResponsibleUser())
				: null;
		
		return new TaskResponse(
				task.getId(), 
				task.getTitle(), 
				task.getDescription(), 
				task.getStatus(), 
				task.getPriority(), 
				task.getDeadline(), 
				task.getCategory(), 
				UserMapper.toUserResponse(task.getOwner()), 
				responsible
		);
	}
}
