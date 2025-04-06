package app.web;

import app.payment.service.PaymentService;
import app.security.AuthenticationMetadata;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerApiTest {
    @MockitoBean
    private PaymentService paymentService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ExceptionAdvice exceptionAdvice;

    @Test
    void putAuthorizedRequestToGetAllCompletedPayment_shouldReturn200_OK() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "maria", "1234", UserRole.ADMIN);
        paymentService.getAllPaymentsByBookingStatus();
        MockHttpServletRequestBuilder request = get("/payments/allCompleted")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("payments-status-completed"))
                .andExpect(model().attributeExists("allPaymentsCompleted"));
    }

    @Test
    void putUnAuthorizedRequestToGetAllCompletedPayment_shouldReturn404andNotFoundView() throws Exception {
        AuthenticationMetadata principal = new AuthenticationMetadata(UUID.randomUUID(), "megi", "1234", UserRole.GUEST);
        paymentService.getAllPaymentsByBookingStatus();
        MockHttpServletRequestBuilder request = get("/payments/allCompleted")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }
}

