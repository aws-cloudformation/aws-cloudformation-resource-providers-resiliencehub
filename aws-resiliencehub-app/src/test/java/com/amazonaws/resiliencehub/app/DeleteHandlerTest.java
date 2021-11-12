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
import software.amazon.awssdk.services.resiliencehub.model.App;
import software.amazon.awssdk.services.resiliencehub.model.DeleteAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DeleteAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

    @Mock
    private ResiliencehubClient sdkClient;

    @Mock
    private ApiCallsWrapper apiCallsWrapper;

    private AmazonWebServicesClientProxy proxy;
    private ProxyClient<ResiliencehubClient> proxyClient;
    private DeleteHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, sdkClient);
        handler = new DeleteHandler(apiCallsWrapper);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final App app = TestDataProvider.app();
        final ResourceModel model = TestDataProvider.resourceModel(app);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final DeleteAppRequest deleteAppRequest = Translator.translateToDeleteAppRequest(model);
        final DeleteAppResponse deleteAppResponse = DeleteAppResponse.builder().appArn(app.appArn()).build();
        final ListAppsRequest listAppsRequest = ListAppsRequest.builder().appArn(app.appArn()).build();

        when(apiCallsWrapper.listApps(eq(listAppsRequest), eq(proxyClient)))
            .thenReturn(ListAppsResponse.builder().appSummaries(Collections.emptyList()).build());
        when(apiCallsWrapper.deleteApp(eq(deleteAppRequest), eq(proxyClient))).thenReturn(deleteAppResponse);

        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.defaultSuccessHandler(null));
    }

    @Test
    public void handleRequest_SuccessAfterStabilize() {
        final App app = TestDataProvider.app();
        final ResourceModel model = TestDataProvider.resourceModel(app);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final DeleteAppRequest deleteAppRequest = Translator.translateToDeleteAppRequest(model);
        final DeleteAppResponse deleteAppResponse = DeleteAppResponse.builder().appArn(app.appArn()).build();
        final ListAppsRequest listAppsRequest = ListAppsRequest.builder().appArn(app.appArn()).build();

        when(apiCallsWrapper.listApps(eq(listAppsRequest), eq(proxyClient)))
            .thenReturn(ListAppsResponse.builder().appSummaries(ImmutableList.of(TestDataProvider.appSummary(app))).build())
            .thenReturn(ListAppsResponse.builder().appSummaries(Collections.emptyList()).build());
        when(apiCallsWrapper.deleteApp(eq(deleteAppRequest), eq(proxyClient))).thenReturn(deleteAppResponse);

        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.defaultSuccessHandler(null));
    }
}
