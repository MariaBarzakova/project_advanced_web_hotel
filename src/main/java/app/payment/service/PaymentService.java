package app.payment.service;

import app.exception.WarningNoPaymentException;
import app.payment.model.Payment;
import app.payment.model.PaymentStatus;
import app.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;


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
                .reduce(BigDecimal::add).orElseThrow(()->new WarningNoPaymentException("There is no Payments yet"));
    }
}
