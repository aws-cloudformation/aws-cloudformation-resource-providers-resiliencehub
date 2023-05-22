package com.amazonaws.resiliencehub.common;

import org.apache.commons.lang3.Validate;

import java.util.function.Supplier;

import software.amazon.awssdk.services.resiliencehub.model.ConflictException;
import software.amazon.awssdk.services.resiliencehub.model.InternalServerException;
import software.amazon.awssdk.services.resiliencehub.model.ResiliencehubException;
import software.amazon.awssdk.services.resiliencehub.model.ResourceNotFoundException;
import software.amazon.awssdk.services.resiliencehub.model.ServiceQuotaExceededException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;

/**
 * Exception Wrapper class for calling ResilienceHub APIs. The primary function of this class
 * is to wrap ResilienceHub service exceptions with the appropriate CloudFormation exception.
 * This is necessary so that CloudFormation can determine whether or not it should
 * retry a failed request.
 */
public class ExceptionHandlerWrapper {

    static final String THROTTLING_ERROR_CODE = "ThrottlingException";
    static final String ACCESS_DENIED_ERROR_CODE = "AccessDeniedException";
    static final String VALIDATION_ERROR_CODE = "ValidationException";

    // prevent instantiation
    private ExceptionHandlerWrapper() {
    }

    public static <T> T wrapResilienceHubExceptions(final String operation, final Supplier<T> serviceCall) {
        Validate.notBlank(operation);
        Validate.notNull(serviceCall);

        try {
            return serviceCall.get();
        } catch (final ConflictException ex) {
            throw new CfnAlreadyExistsException(ex);
        } catch (final ResourceNotFoundException ex) {
            throw new CfnNotFoundException(ex);
        } catch (final ServiceQuotaExceededException ex) {
            throw new CfnServiceLimitExceededException(ex);
        } catch (final InternalServerException ex) {
            throw new CfnInternalFailureException(ex);
        } catch (final ResiliencehubException ex) {
            if (ACCESS_DENIED_ERROR_CODE.equals(ex.awsErrorDetails().errorCode())) {
                throw new CfnAccessDeniedException(operation, ex);
            } else if (THROTTLING_ERROR_CODE.equals(ex.awsErrorDetails().errorCode())) {
                throw new CfnThrottlingException(operation, ex);
            } else if (VALIDATION_ERROR_CODE.equals(ex.awsErrorDetails().errorCode())) {
                throw new CfnInvalidRequestException(ex);
            }
            throw new CfnGeneralServiceException(operation, ex);
        }
    }

}
