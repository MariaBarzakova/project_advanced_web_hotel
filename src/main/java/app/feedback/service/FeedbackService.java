package app.feedback.service;

import app.feedback.model.Feedback;
import app.feedback.repository.FeedbackRepository;
import app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.web.dto.FeedbackRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class FeedbackService {
    private final FeedbackRepository repository;
    @Autowired
    public FeedbackService(FeedbackRepository repository) {
        this.repository = repository;
    }

    public List<Feedback> getAllFeedbacks() {
       //feedbackList.stream().limit(5).toList();
        return repository.findAll();
    }

    public void createFeedback(FeedbackRequest feedbackRequest, User user) {
        //UUID userId=feedbackRequest.getUserId();
        Feedback feedback = Feedback.builder()
                .firstName(feedbackRequest.getFirstName())
                .lastName(feedbackRequest.getLastName())
                .message(feedbackRequest.getMessage())
                .date(LocalDate.now())
                .user(user)
                .build();
        repository.save(feedback);
    }
}
