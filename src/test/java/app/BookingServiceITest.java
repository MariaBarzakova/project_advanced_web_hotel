package app;

import app.booking.model.Booking;
import app.booking.model.BookingStatus;
import app.booking.repository.BookingRepository;
import app.booking.service.BookingService;
import app.exception.BookingException;
import app.feedback.service.FeedbackService;
import app.payment.model.Payment;
import app.payment.model.PaymentStatus;
import app.payment.repository.PaymentRepository;
import app.room.model.Room;
import app.room.model.RoomType;
import app.room.repository.RoomRepository;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.BookingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceITest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void testCreateNewBooking_successfullyPersistsBookingAndPayment() {

        Room room = Room.builder()
                .roomNumber("101")
                .type(RoomType.SINGLE)
                .pricePerNight(new BigDecimal("100.00"))
                .description("Test room")
                .imageUrl("http://example.com/image.jpg")
                .build();
        roomRepository.save(room);

        User user = User.builder()
                .username("testUser")
                .password("password")
                .email("test@example.com")
                .role(UserRole.GUEST)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        userRepository.save(user);


        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(3));
        bookingRequest.setCreatedAt(LocalDateTime.now());
        bookingRequest.setTotalPrice(new BigDecimal("300.00"));
        bookingRequest.setPaymentStatus(PaymentStatus.COMPLETED);
        bookingRequest.setBookingStatus(BookingStatus.BOOKED);


        bookingService.createNewBooking(bookingRequest, user, room.getId());

        Booking savedBooking = bookingRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId()).orElseThrow(RuntimeException::new);
        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getRoom().getId()).isEqualTo(room.getId());
        assertThat(savedBooking.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedBooking.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(savedBooking.getTotalPrice()).isEqualTo(new BigDecimal("200.00"));

        Payment savedPayment = paymentRepository.findByBookingId(savedBooking.getId()).orElseThrow(RuntimeException::new);
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getAmount()).isEqualTo(savedBooking.getTotalPrice());
        assertThat(savedPayment.getUser().getId()).isEqualTo(user.getId());
    }
    @Test
    public void testCreateNewBooking_failsDueToOverlappingBooking() {
        Room room = Room.builder()
                .roomNumber("102")
                .type(RoomType.SINGLE)
                .pricePerNight(new BigDecimal("100.00"))
                .description("Overlap test room")
                .imageUrl("http://example.com/image.jpg")
                .build();
        roomRepository.save(room);

        User user = User.builder()
                .username("overlapUser")
                .password("password")
                .email("overlap@example.com")
                .role(UserRole.GUEST)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        userRepository.save(user);

        // First booking (valid)
        BookingRequest firstBooking = new BookingRequest();
        firstBooking.setCheckInDate(LocalDate.now().plusDays(1));
        firstBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        firstBooking.setCreatedAt(LocalDateTime.now());
        firstBooking.setTotalPrice(new BigDecimal("300.00"));
        firstBooking.setPaymentStatus(PaymentStatus.COMPLETED);
        firstBooking.setBookingStatus(BookingStatus.BOOKED);

        bookingService.createNewBooking(firstBooking, user, room.getId());

        // Second booking (overlapping)
        BookingRequest overlappingBooking = new BookingRequest();
        overlappingBooking.setCheckInDate(LocalDate.now().plusDays(2));
        overlappingBooking.setCheckOutDate(LocalDate.now().plusDays(4));
        overlappingBooking.setCreatedAt(LocalDateTime.now());
        overlappingBooking.setTotalPrice(new BigDecimal("300.00"));
        overlappingBooking.setPaymentStatus(PaymentStatus.COMPLETED);
        overlappingBooking.setBookingStatus(BookingStatus.BOOKED);

        org.junit.jupiter.api.Assertions.assertThrows(BookingException.class, () ->
                bookingService.createNewBooking(overlappingBooking, user, room.getId())
        );
    }
}



