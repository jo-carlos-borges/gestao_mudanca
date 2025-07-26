package br.com.base.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.base.domain.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
	
	@Query("SELECT t FROM Task t WHERE t.owner.id = :ownerId")
    Page<Task> findAllByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);
	
	Optional<Task> findByIdAndOwner_id(Long id, Long ownerId);
}
