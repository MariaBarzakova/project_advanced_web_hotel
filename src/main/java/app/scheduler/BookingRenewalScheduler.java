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
    @Scheduled(fixedDelay = 20000)
    public void renewBookingStatus() {
        List<Booking> failedBookings = bookingService.getFailedPaymentStatus(); //(PaymentStatus.FAILED)
        if (failedBookings.isEmpty()) {
            log.info("No Failed Bookings found for renewal");
            return;
        }
        for (Booking booking : failedBookings) {
            LocalDateTime createdAt = booking.getCreatedAt();
            long day = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
            LocalDate deadLineDate = LocalDate.now().plusDays(2);
            if(day==2 || (booking.getCheckInDate()== deadLineDate && booking.getPaymentStatus().equals(PaymentStatus.FAILED))){
                //booking.setPaymentStatus(PaymentStatus.FAILED);
                booking.setBookingStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            }
        }
    }
}
