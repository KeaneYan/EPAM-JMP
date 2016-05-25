package com.epam.mentoring.restapi.web.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by pengfrancis on 16/5/18.
 */
public class NotFoundError extends HttpException {

    public NotFoundError() {
        super(HttpStatus.NOT_FOUND);
    }
    public NotFoundError(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
