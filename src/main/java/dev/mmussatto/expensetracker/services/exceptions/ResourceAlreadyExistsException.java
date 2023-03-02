/*
 * Created by murilo.mussatto on 01/03/2023
 */

package dev.mmussatto.expensetracker.services.exceptions;

import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException{

    private final String path;

    public ResourceAlreadyExistsException(String path) {
        this.path = path;
    }

    public ResourceAlreadyExistsException(String message, String path) {
        super(message);
        this.path = path;
    }

    public ResourceAlreadyExistsException(String message, Throwable cause, String path) {
        super(message, cause);
        this.path = path;
    }

    public ResourceAlreadyExistsException(Throwable cause, String path) {
        super(cause);
        this.path = path;
    }

    public ResourceAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String path) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.path = path;
    }
}
