package app.web;

import app.exception.UniqueEmail;
import app.exception.WarningNoPaymentException;
import app.payment.model.Payment;
import app.payment.service.PaymentService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;

    @Autowired
    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @GetMapping("/allCompleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllCompletedPayments(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getUserById(authenticationMetadata.getUserId());
        List<Payment> allPaymentsCompleted = paymentService.getAllPaymentsByBookingStatus();
        BigDecimal totalAmount = paymentService.getTotalAmountOfPayments();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("payments-status-completed");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allPaymentsCompleted", allPaymentsCompleted);
        modelAndView.addObject("totalAmount", totalAmount);
        return modelAndView;
    }

    @ExceptionHandler(WarningNoPaymentException.class)
    public ModelAndView handleNoPayment(WarningNoPaymentException exception) {
        String message = exception.getMessage();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("payment-error");
        modelAndView.addObject("message",message);
        return modelAndView;
    }
}
