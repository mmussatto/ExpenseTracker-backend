/*
 * Created by murilo.mussatto on 12/04/2023
 */

package dev.mmussatto.expensetracker.exceptions;

public class InvalidMonthException extends RuntimeException{

    public InvalidMonthException() {
    }

    public InvalidMonthException(String message) {
        super(message);
    }

    public InvalidMonthException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMonthException(Throwable cause) {
        super(cause);
    }

    public InvalidMonthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
