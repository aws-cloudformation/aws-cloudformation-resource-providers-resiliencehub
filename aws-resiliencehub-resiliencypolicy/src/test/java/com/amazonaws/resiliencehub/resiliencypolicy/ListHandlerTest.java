package com.amazonaws.resiliencehub.resiliencypolicy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Collections;

import com.amazonaws.resiliencehub.common.AbstractTestBase;
import com.google.common.collect.ImmutableList;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.ListResiliencyPoliciesRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListResiliencyPoliciesResponse;
import software.amazon.awssdk.services.resiliencehub.model.ResiliencyPolicy;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends AbstractTestBase {

    private static final String NEXT_TOKEN = "nextToken";

    @Mock
    private ResiliencehubClient sdkClient;

    @Mock
    private ApiCallsWrapper apiCallsWrapper;

    private AmazonWebServicesClientProxy proxy;
    private ProxyClient<ResiliencehubClient> proxyClient;
    private ListHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, sdkClient);
        handler = new ListHandler(apiCallsWrapper);
    }

    @Test
    public void handleRequest_moreThanZeroPoliciesPresent() {
        final ResourceModel model = TestDataProvider.getResourceModelWithResiliencyPolicyArn();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final software.amazon.awssdk.services.resiliencehub.model.ResiliencyPolicy resiliencyPolicy =
            TestDataProvider.getResiliencyPolicy();

        final ListResiliencyPoliciesRequest listResiliencyPoliciesRequest = Translator.translateToListRequest(null);
        final ListResiliencyPoliciesResponse listResiliencyPoliciesResponse = ListResiliencyPoliciesResponse.builder()
            .resiliencyPolicies(ImmutableList.of(resiliencyPolicy))
            .nextToken(NEXT_TOKEN)
            .build();

        when(apiCallsWrapper.listResiliencyPolicies(eq(listResiliencyPoliciesRequest), eq(proxyClient)))
            .thenReturn(listResiliencyPoliciesResponse);

        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.builder()
                .resourceModels(Collections.singletonList(buildExpectedResourceModel(resiliencyPolicy)))
                .status(OperationStatus.SUCCESS)
                .nextToken(NEXT_TOKEN)
                .build());
    }

    @Test
    public void handleRequest_noPoliciesPresent() {
        final ResourceModel model = TestDataProvider.getResourceModelWithResiliencyPolicyArn();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ListResiliencyPoliciesRequest listResiliencyPoliciesRequest = Translator.translateToListRequest(null);
        final ListResiliencyPoliciesResponse listResiliencyPoliciesResponse = ListResiliencyPoliciesResponse.builder()
            .resiliencyPolicies(ImmutableList.of())
            .build();

        when(apiCallsWrapper.listResiliencyPolicies(eq(listResiliencyPoliciesRequest), eq(proxyClient)))
            .thenReturn(listResiliencyPoliciesResponse);

        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.builder()
                .resourceModels(Collections.emptyList())
                .status(OperationStatus.SUCCESS)
                .build());
    }

    private ResourceModel buildExpectedResourceModel(final ResiliencyPolicy resiliencyPolicy) {
        return ResourceModel.builder()
            .policyArn(resiliencyPolicy.policyArn())
            .build();
    }
}
