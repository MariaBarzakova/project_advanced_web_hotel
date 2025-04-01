package app.exception;

public class WarningNoPaymentException extends RuntimeException{
    public WarningNoPaymentException() {
    }

    public WarningNoPaymentException(String message) {
        super(message);
    }
}
