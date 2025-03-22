package app.payment.service;

import app.exception.DomainException;
import app.payment.model.Payment;
import app.payment.model.PaymentStatus;
import app.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPaymentsByBookingStatus() {
        List<Payment> allCompleted = paymentRepository.findAllByBooking_PaymentStatus(PaymentStatus.COMPLETED);
        return allCompleted.stream().sorted(Comparator.comparing(Payment::getPaymentDate)).toList();
    }

    public BigDecimal getTotalAmountOfPayments(){
        return getAllPaymentsByBookingStatus().stream().map(Payment::getAmount)
                .reduce(BigDecimal::add).orElseThrow(()->new DomainException("There is no Payments yet"));
    }

    public List<Payment> getAllPaymentsByStatusAndDate() {
        List<Payment> allCompletedToday = paymentRepository.findAllByPaymentStatusAndPaymentDate(PaymentStatus.COMPLETED, LocalDate.now());
        return allCompletedToday.stream().sorted(Comparator.comparing(Payment::getPaymentDate).reversed()).toList();
    }
}
