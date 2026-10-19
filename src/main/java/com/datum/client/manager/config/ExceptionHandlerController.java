package com.datum.client.manager.config;

import com.datum.client.manager.exception.BusinessException;
import com.datum.client.manager.exception.ClientAccessException;
import com.datum.client.manager.exception.NotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.text.MessageFormat;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerController {

    private final Environment environment;

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<BusinessExceptionResponse> businessException(BusinessException exception){

        String message = environment.getProperty(exception.getMessage(), exception.getMessage());
        if(exception.getArgs() != null && exception.getArgs().length > 0){
            message = MessageFormat.format(message, exception.getArgs());
        }
        return ResponseEntity.unprocessableContent().body(BusinessExceptionResponse.builder().code(exception.getMessage()).message(message).build());
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<?> notFoundException(NotFoundException exception){

        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<?> invalidRequest(Exception exception) {
        return ResponseEntity.badRequest().body(
                new InvalidRequestResponse("Request data is invalid.")
        );
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> internalServerError(Exception exception){

        log.error("Handle Error",exception);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(value = ClientAccessException.class)
    public ResponseEntity<?> badGateway(ClientAccessException exception){

        log.error("Rest client error",exception);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }



    @Builder
    public record BusinessExceptionResponse(String code, String message){

    }

    public record InvalidRequestResponse(String message) {
    }
}


