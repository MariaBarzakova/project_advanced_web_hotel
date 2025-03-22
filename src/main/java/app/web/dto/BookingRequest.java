package app.web.dto;

import app.booking.model.BookingStatus;
import app.payment.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    @NotNull
    private PaymentStatus paymentStatus;
    @NotNull
    private LocalDate checkInDate;
    @NotNull
    private LocalDate checkOutDate;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private BigDecimal totalPrice;
    @NotNull
    private BookingStatus bookingStatus;
}
