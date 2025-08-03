package br.com.base.mappers;

import br.com.base.domain.Room;
import br.com.base.records.RoomResponse;

public class RoomMapper {
	
	private RoomMapper() {}

    public static RoomResponse toResponse(Room room) {
        return new RoomResponse(room.getId(), room.getName());
    }
}
