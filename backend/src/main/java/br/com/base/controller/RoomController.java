package br.com.base.controller;

import br.com.base.records.RoomRequest;
import br.com.base.records.RoomResponse;
import br.com.base.services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class RoomController {
	
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        RoomResponse createdRoom = roomService.createRoom(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> findRoomsByUser(@AuthenticationPrincipal UserDetails userDetails) {
        List<RoomResponse> rooms = roomService.findAllByUser(userDetails.getUsername());
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        RoomResponse updatedRoom = roomService.updateRoom(id, request, userDetails.getUsername());
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        roomService.deleteRoom(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
