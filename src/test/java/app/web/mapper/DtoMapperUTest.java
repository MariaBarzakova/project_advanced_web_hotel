package app.web.mapper;

import app.feedback.model.Feedback;
import app.room.model.Room;
import app.room.model.RoomType;
import app.user.model.User;
import app.web.dto.EditRoomRequest;
import app.web.dto.FeedbackResponse;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {
    @Test
    void testMapUserToUserEditRequest() {
        User user = User.builder()
                .firstName("A")
                .lastName("B")
                .phoneNumber("123456789")
                .address("Sofia")
                .passport("A1234567")
                .active(true)
                .build();

        UserEditRequest result = DtoMapper.mapUserToUserEditRequest(user);

        assertNotNull(result);
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(user.getAddress(), result.getAddress());
        assertEquals(user.getPassport(), result.getPassport());
        assertEquals(user.getActive(), result.getActive());
    }

    @Test
    void testMapRoomToEditRoomRequest() {
        Room room = Room.builder()
                .roomNumber("100")
                .type(RoomType.SINGLE)
                .pricePerNight(BigDecimal.TEN)
                .description("Nice")
                .imageUrl("image_url")
                .build();
        EditRoomRequest result = DtoMapper.mapRoomToEditRoomRequest(room);

        assertNotNull(result);
        assertEquals(room.getRoomNumber(), result.getRoomNumber());
        assertEquals(room.getType(), result.getType());
        assertEquals(room.getPricePerNight(), result.getPricePerNight());
        assertEquals(room.getDescription(), result.getDescription());
        assertEquals(room.getImageUrl(), result.getImageUrl());
    }

    @Test
    void testMapFeedbackToFeedbackResponse() {
        Feedback feedback = Feedback.builder()
                .firstName("A")
                .lastName("B")
                .message("Great service!")
                .build();

        FeedbackResponse result = DtoMapper.mapFeedBackToFeedbackResponse(feedback);

        assertNotNull(result);
        assertEquals(feedback.getFirstName(), result.getFirstName());
        assertEquals(feedback.getLastName(), result.getLastName());
        assertEquals(feedback.getMessage(), result.getMessage());
    }

}
