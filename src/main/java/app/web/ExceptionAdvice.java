package app.web;

import app.exception.*;
import app.room.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

@ControllerAdvice
public class ExceptionAdvice {
    private final RoomService roomService;

    public ExceptionAdvice(RoomService roomService) {
        this.roomService = roomService;
    }

    @ExceptionHandler(AlreadyExistException.class)
    public String handleUsernameAlreadyExist(RedirectAttributes redirectAttributes, AlreadyExistException exception) {

        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("alreadyExistMessage", message);
        return "redirect:/register";
    }
    @ExceptionHandler(UniqueEmail.class)
    public String handleUniqueEmail(RedirectAttributes redirectAttributes,UniqueEmail exception) {
        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("uniqueEmail", message);
        return "redirect:/register";
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class
    })
    public ModelAndView handleNotFoundExceptions(Exception exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("not-found");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());
        return modelAndView;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());

        return modelAndView;
    }
}
