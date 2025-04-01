package app.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class BookingException extends RuntimeException{
    public BookingException() {
    }

    public BookingException(String message) {
        super(message);
    }
}
