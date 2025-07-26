package br.com.base.records;

import java.time.LocalDate;

import br.com.base.domain.enums.TaskCategory;
import br.com.base.domain.enums.TaskPriority;
import br.com.base.domain.enums.TaskStatus;

public record TaskResponse (
		
		Long id,
		String title,
		String description,
		TaskStatus status,
		TaskPriority priority,
		LocalDate deadline,
		TaskCategory category,
		UserResponse owner,
		UserResponse responsibleUser
		
	){
}	
