/*
 * Created by murilo.mussatto on 02/03/2023
 */

package dev.mmussatto.expensetracker.controllers.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;

    private int Status;

    private String error;

    private String message;

    private String path;
}
