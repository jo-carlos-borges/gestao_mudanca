package br.com.base.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.base.domain.Task;
import br.com.base.domain.User;
import br.com.base.mappers.TaskMapper;
import br.com.base.records.TaskRequest;
import br.com.base.records.TaskResponse;
import br.com.base.repositories.TaskRepository;
import br.com.base.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
	
	private final TaskRepository taskRepository;
	private final UserRepository userRepository;
	
	@Transactional(readOnly = true)
	public Page<TaskResponse> findTasksByUser(String userEmail, Pageable pageable) {
		User owner = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail: " + userEmail));
		
		Page<Task> tasks = taskRepository.findAllByOwnerId(owner.getId(), pageable);
		return tasks.map(TaskMapper::toResponse);
	}
	
	@Transactional
	public TaskResponse createTask(TaskRequest request, String userEmail) {
		User owner = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail: " + userEmail));
		
		User responsible = null;
		if (request.responsibleUserId() != null) {
			responsible = userRepository.findById(request.responsibleUserId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário responsável não encontrado"));	
		}
		
		Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDeadline(request.deadline());
        task.setCategory(request.category());
        task.setOwner(owner);
        task.setResponsibleUser(responsible);
        task.setDeleted(false);
		
		task = taskRepository.save(task);
		return TaskMapper.toResponse(task);
	} 
	
	
	@Transactional
	public TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail) {
		User owner = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
		
		Task task = taskRepository.findByIdAndOwner_id(taskId, owner.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada para este usuário"));
		
		User responsible = null;
		if (request.responsibleUserId() != null) {
			responsible = userRepository.findById(request.responsibleUserId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário responsável não encontrado"));
		}
		
		task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDeadline(request.deadline());
        task.setCategory(request.category());
        task.setOwner(owner);
        task.setResponsibleUser(responsible);
        
        task = taskRepository.save(task);
		return TaskMapper.toResponse(task);
		
	}
	
	@Transactional
	public void deleteTask(Long taskId, String userEmail) {
		User owner = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail: " + userEmail));
		
		Task task = taskRepository.findByIdAndOwner_id(taskId, owner.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada para este usuário"));
		
		taskRepository.delete(task);
				
	}
	
	
	
}
