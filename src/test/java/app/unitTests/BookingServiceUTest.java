package app.unitTests;

import app.booking.model.Booking;
import app.booking.model.BookingStatus;
import app.booking.repository.BookingRepository;
import app.booking.service.BookingService;
import app.payment.model.Payment;
import app.payment.model.PaymentStatus;
import app.payment.repository.PaymentRepository;
import app.room.model.Room;
import app.room.repository.RoomRepository;
import app.user.model.User;
import app.web.dto.BookingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequest bookingRequest;
    private User user;
    private Room room;
    private UUID roomId;

    @BeforeEach
    void setUp() {
        roomId = UUID.randomUUID();
        user = new User();
        user.setActive(true);
        user.setId(UUID.randomUUID());

        room = new Room();
        room.setId(roomId);
        room.setPricePerNight(BigDecimal.valueOf(100));

        bookingRequest = new BookingRequest();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now().plusDays(5));
        bookingRequest.setPaymentStatus(PaymentStatus.COMPLETED);
        bookingRequest.setBookingStatus(BookingStatus.BOOKED);
    }

    @Test
    void testGetAllBookings() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        booking1.setCreatedAt(LocalDateTime.now().minusDays(2));
        booking2.setCreatedAt(LocalDateTime.now().minusDays(1));

        List<Booking> bookings = Arrays.asList(booking1, booking2);
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        assertTrue(result.get(0).getCreatedAt().isBefore(result.get(1).getCreatedAt()));
    }

    @Test
    void testGetBookingById_BookingExists() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(bookingId);

        assertNotNull(result);
    }

    @Test
    void testGetBookingById_BookingNotFound() {
        UUID bookingId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookingService.getBookingById(bookingId));
        assertEquals("Booking with id %s does not exist.".formatted(bookingId), exception.getMessage());
    }

    @Test
    void testCreateNewBooking_Success_PaymentFailed() {
        bookingRequest.setPaymentStatus(PaymentStatus.FAILED);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(bookingRepository.roomAvailable(roomId, bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())).thenReturn(true);

        bookingService.createNewBooking(bookingRequest, user, roomId);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
    @Test
    void testCreateNewBooking_Success_PaymentCompleted() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(bookingRepository.roomAvailable(roomId, bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())).thenReturn(true);
        bookingService.createNewBooking(bookingRequest, user, roomId);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }


    @Test
    void testCreateNewBooking_RoomNotFound() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookingService.createNewBooking(bookingRequest, user, roomId));
        assertEquals("Room is not found", exception.getMessage());
    }

    @Test
    void testCalculateTotalPrice() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        BigDecimal totalPrice = bookingService.calculateTotalPrice(bookingRequest, roomId);

        long days = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
        BigDecimal expectedPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(days));

        assertEquals(expectedPrice, totalPrice);
    }
}
