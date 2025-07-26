package br.com.base.records;

import br.com.base.domain.enums.TaskCategory;
import br.com.base.domain.enums.TaskPriority;
import br.com.base.domain.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest (
		
		@NotBlank @Size(max = 200)
		String title,
		String description,
		@NotNull
		TaskStatus status,
		@NotNull
		TaskPriority priority,
		@FutureOrPresent
		LocalDate deadline,
		@NotNull
		TaskCategory category,
		Long responsibleUserId
		
	){	
}
