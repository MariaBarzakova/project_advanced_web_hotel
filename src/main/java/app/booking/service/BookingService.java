package app.booking.service;

import app.booking.model.Booking;
import app.booking.repository.BookingRepository;
import app.exception.DomainException;
import app.payment.model.Payment;
import app.payment.repository.PaymentRepository;
import app.room.model.Room;
import app.room.repository.RoomRepository;
import app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.web.dto.BookingRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<Booking> getAllBookings() {
        List<Booking> allBookings = bookingRepository.findAll();
        allBookings.sort(Comparator.comparing(Booking::getCreatedAt));
        return allBookings;
    }

    public Booking getBookingById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new DomainException("Booking with id %s does not exist.".formatted(id)));
    }

    @Transactional
    public void createNewBooking(BookingRequest bookingRequest, User user, UUID roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(()->new DomainException("Room is not found"));

        if (bookingRequest.getCheckInDate().isBefore(LocalDate.now())) {
            throw new DomainException("check in date cannot be before today ");
        }

        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new DomainException("check out date cannot be before check in date ");
        }

        if (bookingRequest.getCheckInDate().isEqual(bookingRequest.getCheckOutDate())) {
            throw new DomainException("check in date cannot be equal to check out date ");
        }

        Boolean available = bookingRepository.roomAvailable(roomId, bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
        if (available == null || !available) {
            throw new DomainException("Room is not available for the selected date ranges");
        }

        if(!user.getActive()){
            return;
        }

        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .paymentStatus(bookingRequest.getPaymentStatus())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .createdAt(LocalDateTime.now())
                .totalPrice(calculateTotalPrice(bookingRequest,roomId))
                .bookingStatus(bookingRequest.getBookingStatus())
                .build();
        bookingRepository.save(booking);

        Payment payment = Payment.builder()
                .amount(booking.getTotalPrice())
                .paymentDate(LocalDate.now())
                .paymentStatus(booking.getPaymentStatus())
                .booking(booking)
                .user(booking.getUser())
                .build();
        paymentRepository.save(payment);
    }
    public BigDecimal calculateTotalPrice(BookingRequest bookingRequest,UUID roomId){
        Room room = roomRepository.findById(roomId).orElseThrow(()->new DomainException("Room is not found"));
        BigDecimal pricePerNight = room.getPricePerNight();
        long days = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }

    public Booking getLatestBookingForUser(UUID userId) {
        return bookingRepository.findTopByUserIdOrderByCreatedAtDesc(userId).orElseThrow(() -> new DomainException("No recent booking found"));
    }

    public List<Booking> getBookingsForSpecifiedRoom(UUID roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    public void deleteBooking(UUID id) {
        bookingRepository.deleteById(id);
    }

}
