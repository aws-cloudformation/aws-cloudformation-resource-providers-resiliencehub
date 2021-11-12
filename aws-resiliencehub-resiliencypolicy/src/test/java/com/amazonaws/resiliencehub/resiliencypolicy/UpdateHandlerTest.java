package com.amazonaws.resiliencehub.resiliencypolicy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import com.amazonaws.resiliencehub.common.AbstractTestBase;
import com.amazonaws.resiliencehub.common.TaggingUtil;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.DescribeResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeResiliencyPolicyResponse;
import software.amazon.awssdk.services.resiliencehub.model.UpdateResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.UpdateResiliencyPolicyResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    private ResiliencehubClient sdkClient;

    @Mock
    private ApiCallsWrapper apiCallsWrapper;

    @Mock
    private TaggingUtil taggingUtil;

    private AmazonWebServicesClientProxy proxy;
    private ProxyClient<ResiliencehubClient> proxyClient;
    private UpdateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, sdkClient);
        handler = new UpdateHandler(apiCallsWrapper, taggingUtil);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ResourceModel inputModel = TestDataProvider.getResourceModelWithResiliencyPolicyArn();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(inputModel)
            .build();

        final software.amazon.awssdk.services.resiliencehub.model.ResiliencyPolicy resiliencyPolicy =
            TestDataProvider.getResiliencyPolicy();

        final UpdateResiliencyPolicyRequest updateResiliencyPolicyRequest = Translator.translateToUpdateRequest(inputModel);
        final UpdateResiliencyPolicyResponse updateResiliencyPolicyResponse = UpdateResiliencyPolicyResponse.builder()
            .policy(resiliencyPolicy)
            .build();

        when(apiCallsWrapper.updateResiliencyPolicy(eq(updateResiliencyPolicyRequest), eq(proxyClient)))
            .thenReturn(updateResiliencyPolicyResponse);

        // Read handler invoked in the Create handler at the end.
        final ResourceModel outputModel = TestDataProvider.getResourceModelWithResiliencyPolicyArn(resiliencyPolicy);
        final DescribeResiliencyPolicyRequest describeResiliencyPolicyRequest = Translator.translateToReadRequest(outputModel);
        final DescribeResiliencyPolicyResponse describeResiliencyPolicyResponse = DescribeResiliencyPolicyResponse.builder()
            .policy(resiliencyPolicy)
            .build();

        when(apiCallsWrapper.describeResiliencyPolicy(eq(describeResiliencyPolicyRequest), eq(proxyClient)))
            .thenReturn(describeResiliencyPolicyResponse);

        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.defaultSuccessHandler(outputModel));
    }
}
