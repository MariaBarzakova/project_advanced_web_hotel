package app.room.initialize;

import app.booking.service.BookingService;
import app.room.model.Room;
import app.room.model.RoomType;
import app.room.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.UUID;

@Profile("!test")
@Configuration
public class RoomInitializer implements CommandLineRunner {
    private final RoomRepository roomRepository;
    //private final BookingService bookingService;

    @Autowired
    public RoomInitializer(RoomRepository roomRepository, BookingService bookingService) {
        this.roomRepository = roomRepository;
        //this.bookingService = bookingService;
    }
    @Override
    public void run(String... args) throws Exception {
        if(roomRepository.count()>0){
            return;
        }
        roomRepository.save(new Room(UUID.randomUUID(), "001", RoomType.SINGLE, BigDecimal.valueOf(80.00), "A cozy and comfortable Single room for solo travelers.", "/images/single room 1.png"));
        roomRepository.save(new Room(UUID.randomUUID(), "002", RoomType.SINGLE, BigDecimal.valueOf(80.00), "A large Single room with extra table.", "/images/single room 2.png"));
        roomRepository.save(new Room(UUID.randomUUID(), "003", RoomType.SINGLE, BigDecimal.valueOf(80.00), "A great and bright Single room with sea view.", "/images/single room 3.png"));
        roomRepository.save(new Room(UUID.randomUUID(), "004", RoomType.DOUBLE, BigDecimal.valueOf(110.00), "A spacious Double room with a double bed.", "/images/double room 1.png"));
        roomRepository.save(new Room(UUID.randomUUID(), "005", RoomType.DOUBLE, BigDecimal.valueOf(110.00), "A Double extra large room with stylish furniture.", "/images/double room 2.png"));
        roomRepository.save(new Room(UUID.randomUUID(), "006", RoomType.SUITE, BigDecimal.valueOf(140.00), "A lovely Suite for office and  rest retreat.", "/images/suite 1.png"));
        roomRepository.save(new Room(UUID.randomUUID(), "007", RoomType.SUITE, BigDecimal.valueOf(140.00), "Luxurious Suite with a beautiful view.", "/images/suite 2.png"));
    }
}
