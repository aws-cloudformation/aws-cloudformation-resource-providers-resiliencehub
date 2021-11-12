package com.amazonaws.resiliencehub.resiliencypolicy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import com.amazonaws.resiliencehub.common.AbstractTestBase;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.CreateResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.CreateResiliencyPolicyResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeResiliencyPolicyResponse;
import software.amazon.awssdk.services.resiliencehub.model.ResiliencyPolicy;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private ResiliencehubClient sdkClient;

    @Mock
    private ApiCallsWrapper apiCallsWrapper;

    private AmazonWebServicesClientProxy proxy;
    private ProxyClient<ResiliencehubClient> proxyClient;
    private CreateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, sdkClient);
        handler = new CreateHandler(apiCallsWrapper);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ResourceModel model = TestDataProvider.getResourceModelWithoutResiliencyPolicyArn();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final software.amazon.awssdk.services.resiliencehub.model.ResiliencyPolicy resiliencyPolicy =
            TestDataProvider.getResiliencyPolicy();

        final CreateResiliencyPolicyRequest createResiliencyPolicyRequest = Translator.translateToCreateRequest(model);
        final CreateResiliencyPolicyResponse createResiliencyPolicyResponse = CreateResiliencyPolicyResponse.builder()
            .policy(resiliencyPolicy)
            .build();

        when(apiCallsWrapper.createResiliencyPolicy(eq(createResiliencyPolicyRequest), eq(proxyClient)))
            .thenReturn(createResiliencyPolicyResponse);

        final CallbackContext context = new CallbackContext();
        final ProgressEvent<ResourceModel, CallbackContext> actualResponse = handler
            .handleRequest(proxy, request, context, proxyClient, logger);
        context.setCreated(true);
        model.setPolicyArn(resiliencyPolicy.policyArn());
        assertEquals(ProgressEvent.defaultInProgressHandler(context, CreateHandler.CALLBACK_DELAY_SECONDS, model), actualResponse);
    }

    @Test
    public void handleRequest_SimpleSuccess_retriedAfterCallbackDelay_PolicyCreated() {
        final ResiliencyPolicy resiliencyPolicy = TestDataProvider.getResiliencyPolicy();
        final ResourceModel model = TestDataProvider.getResourceModelWithResiliencyPolicyArn(resiliencyPolicy);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        // Read handler invoked in the Create handler at the end.
        final DescribeResiliencyPolicyRequest describeResiliencyPolicyRequest = Translator.translateToReadRequest(model);
        final DescribeResiliencyPolicyResponse describeResiliencyPolicyResponse = DescribeResiliencyPolicyResponse.builder()
            .policy(resiliencyPolicy)
            .build();

        when(apiCallsWrapper.describeResiliencyPolicy(eq(describeResiliencyPolicyRequest), eq(proxyClient)))
            .thenReturn(describeResiliencyPolicyResponse);

        final CallbackContext context = new CallbackContext();
        context.setCreated(true);
        assertThat(handler.handleRequest(proxy, request, context, proxyClient, logger))
            .isEqualTo(ProgressEvent.defaultSuccessHandler(model));

        // ResiliencyPolicy was already created and context.isCreated=true
        verify(apiCallsWrapper, never()).createResiliencyPolicy(any(CreateResiliencyPolicyRequest.class), any());
    }
}
