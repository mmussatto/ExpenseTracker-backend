/*
 * Created by murilo.mussatto on 02/03/2023
 */

package dev.mmussatto.expensetracker.services.exceptions;

import lombok.Getter;

/**
 * The message for this exception is the id of the original saved object
 */
@Getter
public class InvalidIdModificationException extends RuntimeException{

    private final String path;

    public InvalidIdModificationException(String path) {
        this.path = path;
    }

    public InvalidIdModificationException(String message, String path) {
        super(message);
        this.path = path;
    }

    public InvalidIdModificationException(String message, Throwable cause, String path) {
        super(message, cause);
        this.path = path;
    }

    public InvalidIdModificationException(Throwable cause, String path) {
        super(cause);
        this.path = path;
    }

    public InvalidIdModificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String path) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.path = path;
    }
}
