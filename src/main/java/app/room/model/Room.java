package app.room.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String roomNumber;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private RoomType type;

    @Column(name = "price_per_night")
    private BigDecimal pricePerNight;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

}
