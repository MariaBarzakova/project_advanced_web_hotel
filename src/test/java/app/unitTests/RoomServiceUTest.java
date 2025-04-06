package app.unitTests;

import app.room.model.Room;
import app.room.model.RoomType;
import app.room.repository.RoomRepository;
import app.room.service.RoomService;
import app.user.model.User;
import app.web.dto.EditRoomRequest;
import app.web.dto.RoomRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceUTest {
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private RoomRequest roomRequest;
    private EditRoomRequest editRoomRequest;
    private Room room;
    private UUID roomId;

    @BeforeEach
    void setUp() {
        roomId = UUID.randomUUID();
        roomRequest = new RoomRequest();
        roomRequest.setRoomNumber("101");
        roomRequest.setType(RoomType.SINGLE);
        roomRequest.setPricePerNight(BigDecimal.valueOf(100));
        roomRequest.setDescription("Cozy single room");
        roomRequest.setImageUrl("image.jpg");

        editRoomRequest = EditRoomRequest.builder()
                .roomNumber("102")
                .type(RoomType.DOUBLE)
                .pricePerNight(BigDecimal.valueOf(150))
                .description("Nice A")
                .imageUrl("image2.jpg")
                .build();
        room = new Room();
        room.setId(roomId);
        room.setRoomNumber("101");
        room.setType(RoomType.SINGLE);
        room.setPricePerNight(BigDecimal.valueOf(100));
        room.setDescription("Cozy single room");
        room.setImageUrl("image.jpg");
    }

    @Test
    void testCreateNewRoom() {
        roomService.createNewRoom(roomRequest, new User());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testEditExistingRoom() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        roomService.editExistingRoom(roomId, editRoomRequest);

        verify(roomRepository, times(1)).save(any(Room.class));
        assertEquals("102", room.getRoomNumber());
        assertEquals(RoomType.DOUBLE, room.getType());
        assertEquals(BigDecimal.valueOf(150), room.getPricePerNight());
        assertEquals("Nice A", room.getDescription());
        assertEquals("image2.jpg", room.getImageUrl());
    }

    @Test
    void testGetRoomById_Success() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        Room result = roomService.getRoomById(roomId);
        assertNotNull(result);
        assertEquals(roomId, result.getId());
    }

    @Test
    void testGetRoomById_NotFound() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> roomService.getRoomById(roomId));
        assertEquals("Room not found", exception.getMessage());
    }

    @Test
    void testGetRoomByRoomNumber_Success() {
        when(roomRepository.findByRoomNumber("101")).thenReturn(Optional.of(room));
        Room result = roomService.getRoomByRoomNumber("101");
        assertNotNull(result);
        assertEquals("101", result.getRoomNumber());
    }

    @Test
    void testGetRoomByRoomNumber_NotFound() {
        when(roomRepository.findByRoomNumber("101")).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> roomService.getRoomByRoomNumber("101"));
        assertEquals("Room not found", exception.getMessage());
    }

    @Test
    void testDeleteRoomById() {
        roomService.deleteRoomById(roomId);
        verify(roomRepository, times(1)).deleteById(roomId);
    }

    @Test
    void testGetAllRooms() {
        Room room2 = new Room();
        room2.setRoomNumber("102");
        room2.setType(RoomType.DOUBLE);
        List<Room> rooms = Arrays.asList(room2, room);
        when(roomRepository.findAll()).thenReturn(rooms);

        List<Room> result = roomService.getAllRooms();
        assertEquals(2, result.size());
        assertTrue(result.get(0).getType().compareTo(result.get(1).getType()) <= 0);
    }
}
