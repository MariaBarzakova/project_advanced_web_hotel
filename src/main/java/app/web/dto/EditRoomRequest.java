package app.web.dto;

import app.room.model.RoomType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class EditRoomRequest {
    @NotNull
    private String roomNumber;
    @NotNull
    private RoomType type;
    @NotNull
    private BigDecimal pricePerNight;
    @NotNull
    private String description;
    @NotNull
    private String imageUrl;
}
