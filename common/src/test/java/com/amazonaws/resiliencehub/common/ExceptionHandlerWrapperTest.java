package com.amazonaws.resiliencehub.common;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.resiliencehub.model.AccessDeniedException;
import software.amazon.awssdk.services.resiliencehub.model.ConflictException;
import software.amazon.awssdk.services.resiliencehub.model.InternalServerException;
import software.amazon.awssdk.services.resiliencehub.model.ResourceNotFoundException;
import software.amazon.awssdk.services.resiliencehub.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.resiliencehub.model.ThrottlingException;
import software.amazon.awssdk.services.resiliencehub.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionHandlerWrapperTest {

    private static final String OPERATION = "operation";

    @Test
    public void testResourceAlreadyExists() {
        final Supplier<String> serviceCall = () -> {
            throw ConflictException.builder().build();
        };
        assertThrows(CfnAlreadyExistsException.class, () ->
            ExceptionHandlerWrapper.wrapResilienceHubExceptions(OPERATION, serviceCall));
    }

    @Test
    public void testResourceNotFound() {
        final Supplier<String> serviceCall = () -> {
            throw ResourceNotFoundException.builder().build();
        };
        assertThrows(CfnNotFoundException.class, () ->
            ExceptionHandlerWrapper.wrapResilienceHubExceptions(OPERATION, serviceCall));
    }

    @Test
    public void testServiceLimitExceeded() {
        final Supplier<String> serviceCall = () -> {
            throw ServiceQuotaExceededException.builder().build();
        };
        assertThrows(CfnServiceLimitExceededException.class, () ->
            ExceptionHandlerWrapper.wrapResilienceHubExceptions(OPERATION, serviceCall));
    }

    @Test
    public void testInternalFailure() {
        final Supplier<String> serviceCall = () -> {
            throw InternalServerException.builder().build();
        };
        assertThrows(CfnInternalFailureException.class, () ->
            ExceptionHandlerWrapper.wrapResilienceHubExceptions(OPERATION, serviceCall));
    }

    @Test
    public void testAccessDenied() {
        final Supplier<String> serviceCall = () -> {
            throw AccessDeniedException.builder()
                .awsErrorDetails(AwsErrorDetails.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder()
                        .statusCode(400)
                        .build())
                    .errorCode(ExceptionHandlerWrapper.ACCESS_DENIED_ERROR_CODE)
                    .build())
                .build();
        };
        assertThrows(CfnAccessDeniedException.class, () ->
            ExceptionHandlerWrapper.wrapResilienceHubExceptions(OPERATION, serviceCall));
    }

    @Test
    public void testThrottlingError() {
        final Supplier<String> serviceCall = () -> {
            throw ThrottlingException.builder()
                .awsErrorDetails(AwsErrorDetails.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder()
                        .statusCode(400)
                        .build())
                    .errorCode(ExceptionHandlerWrapper.THROTTLING_ERROR_CODE)
                    .build())
                .build();
        };
        assertThrows(CfnThrottlingException.class, () ->
            ExceptionHandlerWrapper.wrapResilienceHubExceptions(OPERATION, serviceCall));
    }

    @Test
    public void testValidationError() {
        final Supplier<String> serviceCall = () -> {
            throw ValidationException.builder()
                .awsErrorDetails(AwsErrorDetails.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder()
                        .statusCode(400)
                        .build())
                    .errorCode(ExceptionHandlerWrapper.VALIDATION_ERROR_CODE)
                    .build())
                .build();
        };
        assertThrows(CfnInvalidRequestException.class, () ->
            ExceptionHandlerWrapper.wrapResilienceHubExceptions(OPERATION, serviceCall));
    }

}
