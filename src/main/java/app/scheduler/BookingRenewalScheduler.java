package app.scheduler;

import app.booking.model.Booking;
import app.booking.model.BookingStatus;
import app.booking.repository.BookingRepository;
import app.booking.service.BookingService;
import app.payment.model.PaymentStatus;
import app.payment.service.PaymentService;
import app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class BookingRenewalScheduler {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public BookingRenewalScheduler(BookingService bookingService, BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(fixedDelay = 60000)
    public void renewBookingStatus() {
        List<Booking> failedBookings = bookingService.getFailedPaymentStatus(); //(PaymentStatus.FAILED)
        if (failedBookings.isEmpty()) {
            log.info("No Failed Bookings found for renewal");
            return;
        }
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        for (Booking booking : failedBookings) {

            if (BookingStatus.BOOKED.equals(booking.getBookingStatus()) &&
                    tomorrow.equals(booking.getCheckInDate())) {

                booking.setBookingStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                log.info("Booking [%s] cancelled.".formatted(booking.getId()));
            }
        }
    }
}
