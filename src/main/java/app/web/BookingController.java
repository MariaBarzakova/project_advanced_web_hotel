package app.web;

import app.booking.model.Booking;
import app.booking.service.BookingService;
import app.exception.BookingException;
import app.room.model.Room;
import app.room.service.RoomService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.BookingRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final RoomService roomService;

    @Autowired
    public BookingController(BookingService bookingService, UserService userService, RoomService roomService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.roomService = roomService;
    }

    @GetMapping("/new/{roomId}")
    public ModelAndView registerNewBooking(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                           @PathVariable UUID roomId) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        Room room = roomService.getRoomById(roomId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("booking-new");
        modelAndView.addObject("user", user);
        modelAndView.addObject("room", room);
        modelAndView.addObject("bookingRequest", new BookingRequest());
        return modelAndView;
    }


    @PostMapping("/new/{roomId}")
    public String createBooking(@PathVariable UUID roomId, @Valid BookingRequest bookingRequest,
                                BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (bindingResult.hasErrors()) {
            return "booking-new";
        }

        User user = userService.getUserById(authenticationMetadata.getUserId());

        bookingService.createNewBooking(bookingRequest, user, roomId);
        return "redirect:/bookings/confirmation";
    }


    @GetMapping("/confirmation")
    public ModelAndView bookingConfirmation(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView();

        Booking latestBooking = bookingService.getLatestBookingForUser(authenticationMetadata.getUserId());

        modelAndView.setViewName("booking-confirmation");
        modelAndView.addObject("user", user);
        modelAndView.addObject("latestBooking", latestBooking);
        return modelAndView;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllBookings(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        List<Booking> allBookings = bookingService.getAllBookings();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bookings");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allBookings", allBookings);
        return modelAndView;
    }

    @GetMapping("/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllBookingsByRoomId(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @PathVariable UUID roomId) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        List<Booking> bookingsForARoom = bookingService.getBookingsForSpecifiedRoom(roomId);
        Room room = roomService.getRoomById(roomId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("allBookings-forARoom");
        modelAndView.addObject("user", user);
        modelAndView.addObject("room", room);
        modelAndView.addObject("bookingsForARoom", bookingsForARoom);
        return modelAndView;
    }


    @ExceptionHandler(BookingException.class)
    public String handleBooking(RedirectAttributes redirectAttributes, BookingException exception) {
        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("bookingException",message);
        return "redirect:/bookings/new/{roomId}";
    }

}
