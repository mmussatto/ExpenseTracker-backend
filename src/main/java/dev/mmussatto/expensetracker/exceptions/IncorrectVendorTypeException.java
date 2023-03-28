/*
 * Created by murilo.mussatto on 28/03/2023
 */

package dev.mmussatto.expensetracker.exceptions;

import lombok.Getter;

@Getter
public class IncorrectVendorTypeException extends RuntimeException{

    public IncorrectVendorTypeException() {
    }

    public IncorrectVendorTypeException(String message) {
        super(message);
    }

    public IncorrectVendorTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectVendorTypeException(Throwable cause) {
        super(cause);
    }

    public IncorrectVendorTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
