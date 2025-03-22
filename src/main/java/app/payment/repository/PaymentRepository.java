package app.payment.repository;

import app.payment.model.Payment;
import app.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByBooking_PaymentStatus(PaymentStatus paymentStatus);

    List<Payment> findAllByPaymentStatusAndPaymentDate(PaymentStatus paymentStatus, LocalDate today);

    //@Query("""
    //    SELECT p FROM Payment p
    //    WHERE p.paymentStatus = :status
    //    AND p.paymentDate = :paymentDate
    //""")
    //List<Payment> findByStatusAndDate(@Param("status") PaymentStatus status, @Param("paymentDate") LocalDate paymentDate);

}

