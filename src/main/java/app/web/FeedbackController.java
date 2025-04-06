package app.web;

import app.feedback.model.Feedback;
import app.feedback.service.FeedbackService;
import app.room.model.Room;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.BookingRequest;
import app.web.dto.FeedbackRequest;
import app.web.dto.FeedbackResponse;
import app.web.dto.RoomRequest;
import app.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;
    private final UserService userService;

    @Autowired
    public FeedbackController(FeedbackService service, UserService userService) {
        this.feedbackService = service;
        this.userService = userService;
    }

    @GetMapping("/new")
    public ModelAndView registerNewFeedback(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getUserById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("feedback-form");
        modelAndView.addObject("user", user);
        modelAndView.addObject("feedbackRequest", FeedbackRequest.builder().build());
        return modelAndView;
    }


    @PostMapping("/new")
    public String createNewFeedback(@Valid FeedbackRequest feedbackRequest, BindingResult bindingResult,
                                    @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        if (bindingResult.hasErrors()) {
            return "feedback-form";
        }
        feedbackService.createFeedback(feedbackRequest, user);
        return "redirect:/home";
    }

        @GetMapping("/all")
    public ModelAndView getAllFeedbacksPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getUserById(authenticationMetadata.getUserId());
        List<Feedback> allFeedbacks = feedbackService.getAllFeedbacks();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allFeedbacks", allFeedbacks);
        return modelAndView;
    }
}

