package br.com.base.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.base.domain.Room;
import br.com.base.domain.User;
import br.com.base.mappers.RoomMapper;
import br.com.base.records.RoomRequest;
import br.com.base.records.RoomResponse;
import br.com.base.repositories.RoomRepository;
import br.com.base.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
	
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	
	@Transactional(readOnly = true)
	public List<RoomResponse> findAllByUser(String userEmail) {
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + userEmail));
		
		return roomRepository.findAllByUser_Id(user.getId())
				.stream()
				.map(RoomMapper::toResponse)
				.collect(Collectors.toList());
	}
	
	
	@Transactional
	public RoomResponse createRoom(RoomRequest request, String userEmail) {
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + userEmail));
		
		Room room = new Room();
		room.setName(request.name());
		room.setUser(user);
		
		room = roomRepository.save(room);
		return RoomMapper.toResponse(room);
	}
	
	@Transactional
    public RoomResponse updateRoom(Long roomId, RoomRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + userEmail));
     
        Room room = roomRepository.findByIdAndUser_Id(roomId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quarto não encontrado"));
        
        room.setName(request.name());
        room = roomRepository.save(room);
        return RoomMapper.toResponse(room);
	}
	
	@Transactional
    public void deleteRoom(Long roomId, String userEmail) {
		User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + userEmail));
     
        Room room = roomRepository.findByIdAndUser_Id(roomId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quarto não encontrado"));
        
        roomRepository.delete(room);
    }

}
