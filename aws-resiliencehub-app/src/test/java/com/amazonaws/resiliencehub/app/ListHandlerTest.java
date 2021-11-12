package com.amazonaws.resiliencehub.app;

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
import software.amazon.awssdk.services.resiliencehub.model.AppSummary;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsResponse;
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
    public void handleRequest_moreThanZeroAppsPresent() {
        final AppSummary appSummary = TestDataProvider.appSummary();
        final ListAppsRequest listAppsRequest = Translator.translateToListAppRequest(null);
        final ListAppsResponse listAppsResponse = ListAppsResponse.builder()
            .appSummaries(ImmutableList.of(appSummary))
            .nextToken(NEXT_TOKEN)
            .build();

        final ResourceModel model = TestDataProvider.resourceModel(appSummary);
        model.setTags(null); // Unsetting the fields that aren't returned in the ListApp response.
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(apiCallsWrapper.listApps(eq(listAppsRequest), eq(proxyClient))).thenReturn(listAppsResponse);

        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.builder()
                .status(OperationStatus.SUCCESS)
                .resourceModels(Collections.singletonList(model))
                .nextToken(NEXT_TOKEN)
                .build());
    }

    @Test
    public void handleRequest_noAppsPresent() {
        final AppSummary appSummary = TestDataProvider.appSummary();
        final ListAppsRequest listAppsRequest = Translator.translateToListAppRequest(null);
        final ListAppsResponse listAppsResponse = ListAppsResponse.builder().appSummaries(ImmutableList.of()).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(TestDataProvider.resourceModel(appSummary))
            .build();

        when(apiCallsWrapper.listApps(eq(listAppsRequest), eq(proxyClient))).thenReturn(listAppsResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response =
            handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.builder()
                .status(OperationStatus.SUCCESS)
                .resourceModels(Collections.emptyList())
                .build());
    }
}
