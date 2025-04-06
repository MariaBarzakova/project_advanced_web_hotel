package app.web.dto;

import app.room.model.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.aspectj.bridge.IMessage;
import org.hibernate.validator.constraints.URL;

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
    @NotBlank
    @URL
    private String imageUrl;
}
