package app.exception;

public class EmployeeFeignException extends RuntimeException{
    public EmployeeFeignException() {
    }

    public EmployeeFeignException(String message) {
        super(message);
    }
}
