package app.web;

import app.booking.service.BookingService;
import app.room.service.RoomService;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(BookingController.class)
public class BookingControllerApiTest {
    @MockitoBean
    private BookingService bookingService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private RoomService roomService;

    @Autowired
    private MockMvc mockMvc;

}
