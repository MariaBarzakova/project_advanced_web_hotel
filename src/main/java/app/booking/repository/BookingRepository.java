package app.booking.repository;

import app.booking.model.Booking;
import app.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByRoomId(UUID roomId);

    @Query("""
                   SELECT COUNT(b) = 0 FROM Booking b
                   WHERE b.room.id = :roomId
                   AND :checkInDate <= b.checkOutDate
                   AND :checkOutDate >= b.checkInDate
                   AND (b.bookingStatus = app.booking.model.BookingStatus.BOOKED OR b.bookingStatus =app.booking.model.BookingStatus.CHECKED_IN)
            """)
    Boolean roomAvailable(@Param("roomId") UUID roomId,
                          @Param("checkInDate") LocalDate checkInDate,
                          @Param("checkOutDate") LocalDate checkOutDate);


    Optional<Booking> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Booking> findAllByPaymentStatus(PaymentStatus paymentStatus);
}
