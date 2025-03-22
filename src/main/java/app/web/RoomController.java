package app.web;

import app.room.model.Room;
import app.room.service.RoomService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.EditRoomRequest;
import app.web.dto.RoomRequest;
import app.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;
    private final UserService userService;

    @Autowired
    public RoomController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView getAllRoomsPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        List<Room> allRooms = roomService.getAllRooms();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("rooms");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allRooms", allRooms);
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getRoomDetails(@PathVariable UUID id) {
        Room roomDetails = roomService.getRoomById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("room-details");
        modelAndView.addObject("roomDetails", roomDetails);
        return modelAndView;
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView registerNewRoom(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("room-new");
        modelAndView.addObject("user", user);
        modelAndView.addObject("roomRequest", new RoomRequest());
        return modelAndView;
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView createRoom(@Valid RoomRequest roomRequest, BindingResult bindingResult,
                                   @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("room-new");
            modelAndView.addObject("user",user);
            return modelAndView;
        }
        roomService.createNewRoom(roomRequest,user);
        return new ModelAndView("redirect:/rooms");
    }


    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getEditRoomPage(@PathVariable UUID id,
                                        @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        Room room = roomService.getRoomById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("room-edit");
        modelAndView.addObject("user", user);
        modelAndView.addObject("room", room);
        modelAndView.addObject("editRoomRequest", DtoMapper.mapRoomToEditRoomRequest(room));
        return modelAndView;
    }

    @PutMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView editRoom(@PathVariable UUID id, @Valid EditRoomRequest editRoomRequest,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("room-edit");
            modelAndView.addObject("user",user);
            modelAndView.addObject("editRoomRequest", editRoomRequest);
            return modelAndView;
        }
        roomService.editExistingRoom(id,editRoomRequest);
        return new ModelAndView("redirect:/rooms");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteNewRoomById(@PathVariable UUID id) {
        roomService.deleteRoomById(id);
        return "redirect:/rooms";
    }
}
