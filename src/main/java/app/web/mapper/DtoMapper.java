package app.web.mapper;

import app.feedback.model.Feedback;
import app.room.model.Room;
import app.user.model.User;
import app.web.dto.EditRoomRequest;
import app.web.dto.FeedbackResponse;
import app.web.dto.UserEditRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {
    public static UserEditRequest mapUserToUserEditRequest(User user){
        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .passport(user.getPassport())
                .active(user.getActive())
                .build();
    }

    public static EditRoomRequest mapRoomToEditRoomRequest(Room room){
        return EditRoomRequest.builder()
                .roomNumber(room.getRoomNumber())
                .type(room.getType())
                .pricePerNight(room.getPricePerNight())
                .description(room.getDescription())
                .imageUrl(room.getImageUrl())
                .build();
    }

    public static FeedbackResponse mapFeedBackToFeedbackResponse(Feedback feedback){
        return FeedbackResponse.builder()
                .firstName(feedback.getFirstName())
                .lastName(feedback.getLastName())
                .message(feedback.getMessage())
                .build();
    }
}
