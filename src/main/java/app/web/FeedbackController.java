package app.web;

import app.feedback.model.Feedback;
import app.feedback.service.FeedbackService;
import app.user.model.User;
import app.web.dto.FeedbackRequest;
import app.web.dto.FeedbackResponse;
import app.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService service) {
        this.feedbackService = service;
    }
    @PostMapping
    public ResponseEntity<FeedbackResponse> sendFeedback(@RequestBody FeedbackRequest feedbackRequest, User user){
        Feedback feedback = feedbackService.postFeedback(feedbackRequest,user);
        FeedbackResponse response = DtoMapper.mapFeedBackToFeedbackResponse(feedback);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAll(){
        List<FeedbackResponse> feedbackResponseList = feedbackService.getAllFeedbacks().stream().map(DtoMapper::mapFeedBackToFeedbackResponse).toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(feedbackResponseList);
    }
}
