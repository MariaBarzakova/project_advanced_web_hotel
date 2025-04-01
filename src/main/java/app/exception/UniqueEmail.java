package app.exception;

public class UniqueEmail extends RuntimeException{
    public UniqueEmail() {
    }

    public UniqueEmail(String message) {
        super(message);
    }
}
