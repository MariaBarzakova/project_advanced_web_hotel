package app.web.dto;

import app.room.model.RoomType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomRequest {
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
