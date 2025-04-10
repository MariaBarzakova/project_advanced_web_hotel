package app.unitTests;

import app.exception.WarningNoPaymentException;
import app.payment.model.Payment;
import app.payment.model.PaymentStatus;
import app.payment.repository.PaymentRepository;
import app.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceUTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment1;
    private Payment payment2;

    @BeforeEach
    void setUp() {
        payment1 = new Payment();
        payment1.setPaymentDate(LocalDate.of(2024, 4, 1));
        payment1.setAmount(BigDecimal.valueOf(150));

        payment2 = new Payment();
        payment2.setPaymentDate(LocalDate.of(2024, 4, 2));
        payment2.setAmount(BigDecimal.valueOf(250));
    }

    @Test
    void testGetAllPaymentsByBookingStatus() {
        List<Payment> payments = Arrays.asList(payment2, payment1);
        when(paymentRepository.findAllByBooking_PaymentStatus(PaymentStatus.COMPLETED)).thenReturn(payments);

        List<Payment> result = paymentService.getAllPaymentsByBookingStatus();

        assertEquals(2, result.size());
        assertTrue(result.get(0).getPaymentDate().isBefore(result.get(1).getPaymentDate()));
    }

    @Test
    void testGetTotalAmountOfPayments_Success() {
        when(paymentRepository.findAllByBooking_PaymentStatus(PaymentStatus.COMPLETED)).thenReturn(Arrays.asList(payment1, payment2));

        BigDecimal totalAmount = paymentService.getTotalAmountOfPayments();
        assertEquals(BigDecimal.valueOf(400), totalAmount);
    }

    @Test
    void testGetTotalAmountOfPayments_NoPayments() {
        when(paymentRepository.findAllByBooking_PaymentStatus(PaymentStatus.COMPLETED)).thenReturn(Collections.emptyList());

        WarningNoPaymentException exception = assertThrows(WarningNoPaymentException.class,
                paymentService::getTotalAmountOfPayments);
        assertEquals("There is no Payments yet", exception.getMessage());
    }
}

