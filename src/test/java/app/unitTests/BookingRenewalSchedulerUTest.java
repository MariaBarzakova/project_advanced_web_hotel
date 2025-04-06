package app.unitTests;

import app.booking.model.Booking;
import app.booking.model.BookingStatus;
import app.booking.repository.BookingRepository;
import app.booking.service.BookingService;
import app.payment.model.PaymentStatus;
import app.scheduler.BookingRenewalScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingRenewalSchedulerUTest {
    @Mock
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingRenewalScheduler bookingRenewalScheduler;

    private Booking failedBooking;

    @BeforeEach
    void setUp() {
        failedBooking = new Booking();
        failedBooking.setCreatedAt(LocalDateTime.now().minusDays(2)); // Simulating a failed booking older than 1 day
        failedBooking.setCheckInDate(LocalDate.now().plusDays(1));
        failedBooking.setPaymentStatus(PaymentStatus.FAILED);
        failedBooking.setBookingStatus(BookingStatus.BOOKED);
    }

    @Test
    void testRenewBookingStatus_WithFailedBookings() {
        when(bookingService.getFailedPaymentStatus()).thenReturn(Arrays.asList(failedBooking));
        long days = 1L;
        bookingRenewalScheduler.renewBookingStatus();

        assertEquals(BookingStatus.CANCELLED, failedBooking.getBookingStatus());
        verify(bookingRepository, times(1)).save(failedBooking);
    }

    @Test
    void testRenewBookingStatus_NoFailedBookings() {
        when(bookingService.getFailedPaymentStatus()).thenReturn(Collections.emptyList());

        bookingRenewalScheduler.renewBookingStatus();

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testRenewBookingStatus_CheckInDateDeadline() {
        failedBooking.setCreatedAt(LocalDateTime.now());
        failedBooking.setCheckInDate(LocalDate.now().plusDays(2));

        when(bookingService.getFailedPaymentStatus()).thenReturn(Arrays.asList(failedBooking));

        bookingRenewalScheduler.renewBookingStatus();

        //assertEquals(BookingStatus.CANCELLED, failedBooking.getBookingStatus());
        verify(bookingRepository, times(1)).save(failedBooking);
    }
}
