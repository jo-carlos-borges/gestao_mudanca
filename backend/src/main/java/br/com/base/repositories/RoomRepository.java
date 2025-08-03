package br.com.base.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.base.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
	
	List<Room> findAllByUser_Id(Long userId);
	
	Optional<Room> findByIdAndUser_Id(Long id, Long userId);
}
