package com.voxloud.provisioning.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ProvisioningExceptionHandler {

    @ExceptionHandler(value = DeviceModelNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleDeviceModelNotSupportedException(DeviceModelNotSupportedException e) {
       return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = DeviceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleDeviceNotFoundException(DeviceNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = InvalidOverrideConfigurationException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleInvalidOverrideConfigurationException(InvalidOverrideConfigurationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @AllArgsConstructor
    @Data
    public static class ErrorResponse {
        private String errorMessage;
    }
}
