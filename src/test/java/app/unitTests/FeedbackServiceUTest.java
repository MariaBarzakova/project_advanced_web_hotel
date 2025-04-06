package app.unitTests;

import app.feedback.model.Feedback;
import app.feedback.repository.FeedbackRepository;
import app.feedback.service.FeedbackService;
import app.user.model.User;
import app.web.dto.FeedbackRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceUTest {
    @Mock
    private FeedbackRepository repository;

    @InjectMocks
    private FeedbackService feedbackService;

    private Feedback feedback1;
    private Feedback feedback2;

    @Test
    void givenExistingFeedbacks_whenGetAll_thenReturnThemAll(){
        List<Feedback> feedbackList = List.of(new Feedback(),new Feedback());
        when(repository.findAll()).thenReturn(feedbackList);
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        assertNotNull(feedbacks);
        assertThat(feedbacks).hasSize(2);
    }
    @Test
    void testCreateFeedback() {
        FeedbackRequest feedbackRequest = FeedbackRequest.builder()
                .firstName("A")
                .lastName("B")
                .message("Nice experience!")
                .build();
        User user = new User();
        feedbackService.createFeedback(feedbackRequest, user);
        verify(repository, times(1)).save(any(Feedback.class));
    }
}
