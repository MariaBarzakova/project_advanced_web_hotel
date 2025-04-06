package app.room.service;

import app.room.model.Room;
import app.room.model.RoomType;
import app.room.repository.RoomRepository;
import app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import app.web.dto.EditRoomRequest;
import app.web.dto.RoomRequest;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RoomService {
    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void createNewRoom(RoomRequest roomRequest, User user) {
        RoomType typeOfRoom = roomRequest.getType();
        Room room = Room.builder()
                .roomNumber(roomRequest.getRoomNumber())
                .type(typeOfRoom)
                .pricePerNight(roomRequest.getPricePerNight())
                .description(roomRequest.getDescription())
                .imageUrl(roomRequest.getImageUrl())
                .build();
        roomRepository.save(room);
        log.info("Successfully created new room");
    }

    public void editExistingRoom(UUID roomId, EditRoomRequest editRoomRequest) {
        Room room = getRoomById(roomId);
        room.setRoomNumber(editRoomRequest.getRoomNumber());
        room.setType(editRoomRequest.getType());
        room.setPricePerNight(editRoomRequest.getPricePerNight());
        room.setDescription(editRoomRequest.getDescription());
        room.setImageUrl(editRoomRequest.getImageUrl());
        roomRepository.save(room);
    }

    public Room getRoomById(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }
    public Room getRoomByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber).orElseThrow(()->new RuntimeException("Room not found"));
    }

    public void deleteRoomById(UUID id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream().sorted(Comparator.comparing(Room::getType).thenComparing(Room::getRoomNumber)).toList();
    }
}
