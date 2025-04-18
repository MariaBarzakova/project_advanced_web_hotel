package app.web;

import app.room.model.Room;
import app.room.model.RoomType;
import app.room.service.RoomService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
public class RoomControllerApiTest {
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private RoomService roomService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToGetAllRooms_shouldReturnServerError() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        when(roomService.getAllRooms()).thenThrow(new TemplateProcessingException("null"));
        MockHttpServletRequestBuilder request = get("/rooms")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andExpect(view().name("internal-server-error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void postRequestToCreateARoom_AuthorizedAdmin_shouldReturnNewRoomView() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = post("/rooms/new")
                .with(user(principal))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("room-new"))
                .andExpect(model().attributeExists("roomRequest"));
    }


    @Test
    void getRequestToCreateARoom_AuthorizedAdmin_shouldRedirect() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = get("/rooms/new")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("room-new"))
                .andExpect(model().attributeExists("roomRequest"));
    }

    @Test
    void delete_AuthorizedAdmin_shouldRedirect() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = delete("/rooms/{id}", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms"));
    }

    @Test
    void putRequestToEditARoom_AuthorizedAdmin_shouldReturnSameView() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        MockHttpServletRequestBuilder request = put("/rooms/{id}/edit", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("room-edit"))
                .andExpect(model().attributeExists("editRoomRequest"));
    }

    @Test
    void getRequestToEditARoom_AuthorizedAdmin_shouldReturnError() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        when(roomService.getAllRooms()).thenThrow(new NullPointerException("null"));
        MockHttpServletRequestBuilder request = get("/rooms/{id}/edit", UUID.randomUUID())
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andExpect(view().name("internal-server-error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void getRequestToRoomDetails_shouldReturnRoomDetails() throws Exception {
        UUID roomId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);

        Room mockRoom = new Room();
        when(roomService.getRoomById(roomId)).thenReturn(mockRoom);

        MockHttpServletRequestBuilder request = get("/rooms/{id}", roomId)
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("room-details"))
                .andExpect(model().attributeExists("roomDetails"));
    }

    @Test
    void putRequestToEditRoom_WithValidInput_shouldRedirectToRooms() throws Exception {
        UUID roomId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "admin", "pass", UserRole.ADMIN);

        mockMvc.perform(put("/rooms/{id}/edit", roomId)
                .with(user(principal))
                .with(csrf())
                .param("roomNumber", "100")
                .param("type", "RoomType.SINGLE")
                .param("pricePerNight", "100")
                .param("description", "test")
                .param("imageUrl", "http://example.com/image.jpg"));
    }

}
