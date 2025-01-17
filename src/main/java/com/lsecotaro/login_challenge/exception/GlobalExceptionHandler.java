package com.lsecotaro.login_challenge.exception;

import com.lsecotaro.login_challenge.exception.dto.ApiErrorCode;
import com.lsecotaro.login_challenge.exception.dto.ErrorDetailDto;
import com.lsecotaro.login_challenge.exception.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorDetailDto> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(ErrorDetailDto.builder()
                            .timestamp(new Date())
                            .code(ApiErrorCode.REQUIRED_PARAM_MISSING.getCode())
                            .detail(errorMessage)
                    .build());
        });
        ErrorResponseDto response = ErrorResponseDto.builder()
                .errors(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(InvalidPasswordException ex) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .errors(List.of(ErrorDetailDto.builder()
                        .timestamp(new Date())
                        .code(ApiErrorCode.INVALID_PASSWORD.getCode())
                        .detail(ApiErrorCode.INVALID_PASSWORD.getDescription())
                        .build()))
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(UserAlreadyExistsException ex) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .errors(List.of(ErrorDetailDto.builder()
                                .timestamp(new Date())
                                .code(ApiErrorCode.USER_ALREADY_EXISTS.getCode())
                                .detail(ApiErrorCode.USER_ALREADY_EXISTS.getDescription())
                        .build()))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
