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
import java.util.List;
import java.util.UUID;

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

    @Test
    void testBookingCancelledIfCheckInTomorrowAndFailedPayment() {
        // Given
        Booking booking = Booking.builder()
                .id(UUID.randomUUID())
                .checkInDate(LocalDate.now().plusDays(1))
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.FAILED)
                .build();

        List<Booking> failedBookings = List.of(booking);

        when(bookingService.getFailedPaymentStatus()).thenReturn(failedBookings);

        // When
        bookingRenewalScheduler.renewBookingStatus();

        // Then
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void testBookingNotCancelledIfCheckInIsNotTomorrow() {
        Booking booking = Booking.builder()
                .id(UUID.randomUUID())
                .checkInDate(LocalDate.now().plusDays(3)) // Not tomorrow
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.FAILED)
                .build();

        when(bookingService.getFailedPaymentStatus()).thenReturn(List.of(booking));

        bookingRenewalScheduler.renewBookingStatus();

        assertEquals(BookingStatus.BOOKED, booking.getBookingStatus()); // No change
        verify(bookingRepository, never()).save(any());
    }
    @Test
    void testAlreadyCancelledBookingIsNotTouched() {
        Booking booking = Booking.builder()
                .id(UUID.randomUUID())
                .checkInDate(LocalDate.now().plusDays(1))
                .bookingStatus(BookingStatus.CANCELLED)
                .paymentStatus(PaymentStatus.FAILED)
                .build();

        when(bookingService.getFailedPaymentStatus()).thenReturn(List.of(booking));

        bookingRenewalScheduler.renewBookingStatus();

        // Assert it wasn't modified
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testNullCheckInDateHandledGracefully() {
        Booking booking = Booking.builder()
                .id(UUID.randomUUID())
                .checkInDate(null)
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.FAILED)
                .build();

        when(bookingService.getFailedPaymentStatus()).thenReturn(List.of(booking));

        assertDoesNotThrow(() -> bookingRenewalScheduler.renewBookingStatus());
        verify(bookingRepository, never()).save(any());
    }
    @Test
    void testBookingAlreadyCancelledAndPaymentCompletedIsIgnored() {
        // Arrange
        Booking booking = Booking.builder()
                .id(UUID.randomUUID())
                .checkInDate(LocalDate.now().plusDays(1))
                .bookingStatus(BookingStatus.CANCELLED) // Already cancelled
                .paymentStatus(PaymentStatus.COMPLETED) // Payment OK
                .build();

        when(bookingService.getFailedPaymentStatus()).thenReturn(List.of(booking));

        // Act
        bookingRenewalScheduler.renewBookingStatus();

        // Assert
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
        verify(bookingRepository, never()).save(any()); // Should NOT trigger a DB save
    }
}

