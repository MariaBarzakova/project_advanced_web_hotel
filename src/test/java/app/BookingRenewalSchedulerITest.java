package app;

import app.booking.model.Booking;
import app.booking.model.BookingStatus;
import app.booking.repository.BookingRepository;
import app.booking.service.BookingService;
import app.payment.model.PaymentStatus;
import app.scheduler.BookingRenewalScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingRenewalSchedulerITest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRenewalScheduler bookingRenewalScheduler;

//    @BeforeEach
//    void clean() {
//        bookingRepository.deleteAll();
//    }

    @Test
    void testSchedulerCancelsCorrectBooking() {

        Booking booking = Booking.builder()
                .checkInDate(LocalDate.now().plusDays(1))
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.FAILED)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        bookingRepository.save(booking);

        bookingRenewalScheduler.renewBookingStatus();
        Booking updated = bookingRepository.findById(booking.getId()).orElseThrow(RuntimeException::new);
        assertEquals(BookingStatus.CANCELLED, updated.getBookingStatus());
    }
}
