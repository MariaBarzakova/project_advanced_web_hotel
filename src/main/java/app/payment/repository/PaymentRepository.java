package app.payment.repository;

import app.payment.model.Payment;
import app.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findAllByBooking_PaymentStatus(PaymentStatus paymentStatus);

    Optional<Payment> findByBookingId(UUID id);


}

