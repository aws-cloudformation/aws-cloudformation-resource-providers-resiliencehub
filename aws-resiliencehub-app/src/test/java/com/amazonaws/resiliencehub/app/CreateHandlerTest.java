package com.amazonaws.resiliencehub.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import com.amazonaws.resiliencehub.common.AbstractTestBase;
import com.amazonaws.resiliencehub.common.Constants;
import com.google.common.collect.ImmutableSet;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.AddDraftAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.AddDraftAppVersionResourceMappingsResponse;
import software.amazon.awssdk.services.resiliencehub.model.App;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppVersionTemplateRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppVersionTemplateResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppVersionResourceMappingsResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.resiliencehub.model.PublishAppVersionRequest;
import software.amazon.awssdk.services.resiliencehub.model.PublishAppVersionResponse;
import software.amazon.awssdk.services.resiliencehub.model.PutDraftAppVersionTemplateRequest;
import software.amazon.awssdk.services.resiliencehub.model.PutDraftAppVersionTemplateResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private ResiliencehubClient sdkClient;

    private AmazonWebServicesClientProxy proxy;
    private ProxyClient<ResiliencehubClient> proxyClient;
    private CreateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, sdkClient);
        handler = new CreateHandler();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateAppRequest createAppRequest = TestDataProvider.createAppRequest();
        final CreateAppResponse createAppResponse = TestDataProvider.createAppResponse(createAppRequest);
        final ResourceModel model = TestDataProvider.resourceModel(createAppResponse.app());
        model.setAppTemplateBody(TestDataProvider.APP_TEMPLATE);
        model.setResourceMappings(
            Translator.toCfnResourceMappings(ImmutableSet.of(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING)));
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        when(proxyClient.injectCredentialsAndInvokeV2(createAppRequest, proxyClient.client()::createApp)).thenReturn(createAppResponse);

        final CallbackContext context = new CallbackContext();
        final ProgressEvent<ResourceModel, CallbackContext> actualResponse = handler
            .handleRequest(proxy, request, context, proxyClient, logger);
        context.setCreated(true);
        assertEquals(ProgressEvent.defaultInProgressHandler(context, CreateHandler.CALLBACK_DELAY_SECONDS, model), actualResponse);
    }

    @Test
    public void handleRequest_SimpleSuccess_retriedAfterCallbackDelay_AppCreated() {
        final App app = TestDataProvider.app();
        final ResourceModel model = TestDataProvider.resourceModel(app);
        model.setAppTemplateBody(TestDataProvider.APP_TEMPLATE);
        model.setResourceMappings(
            Translator.toCfnResourceMappings(ImmutableSet.of(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING)));
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final PutDraftAppVersionTemplateRequest appVersionTemplateRequest = PutDraftAppVersionTemplateRequest.builder()
            .appArn(app.appArn())
            .appTemplateBody(TestDataProvider.APP_TEMPLATE)
            .build();
        final PutDraftAppVersionTemplateResponse appVersionTemplateResponse = PutDraftAppVersionTemplateResponse
            .builder().build();
        final AddDraftAppVersionResourceMappingsRequest addResourceMappingsRequest = AddDraftAppVersionResourceMappingsRequest
            .builder()
            .appArn(app.appArn())
            .resourceMappings(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING)
            .build();
        final AddDraftAppVersionResourceMappingsResponse addResourceMappingsResponse = AddDraftAppVersionResourceMappingsResponse
            .builder().build();
        final PublishAppVersionRequest publishAppVersionRequest = PublishAppVersionRequest.builder()
            .appArn(app.appArn())
            .build();
        final PublishAppVersionResponse publishAppVersionResponse = PublishAppVersionResponse.builder()
            .appArn(app.appArn())
            .appVersion(Constants.RELEASE_VERSION)
            .build();

        when(proxyClient.injectCredentialsAndInvokeV2(appVersionTemplateRequest, proxyClient.client()::putDraftAppVersionTemplate))
            .thenReturn(appVersionTemplateResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(addResourceMappingsRequest,
            proxyClient.client()::addDraftAppVersionResourceMappings)).thenReturn(addResourceMappingsResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(publishAppVersionRequest, proxyClient.client()::publishAppVersion))
            .thenReturn(publishAppVersionResponse);

        // Read handler invoked in the Create handler at the end
        final DescribeAppRequest describeAppRequest = Translator.translateToReadAppRequest(model);
        final DescribeAppResponse describeAppResponse = TestDataProvider.describeAppResponse(app);
        final ListTagsForResourceRequest listTagsForResourceRequest = Translator.translateToListTagsForResourceRequest(model);
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder()
            .tags(app.tags()).build();
        final DescribeAppVersionTemplateRequest describeAppVersionTemplateRequest = Translator
            .translateToDescribeAppVersionTemplateRequest(model);
        final DescribeAppVersionTemplateResponse describeAppVersionTemplateResponse = DescribeAppVersionTemplateResponse
            .builder()
            .appArn(app.appArn())
            .appVersion(Constants.RELEASE_VERSION)
            .appTemplateBody(TestDataProvider.APP_TEMPLATE)
            .build();
        final ListAppVersionResourceMappingsRequest listResourceMappingsRequest = Translator
            .translateToListAppVersionResourceMappingsRequest(Constants.RELEASE_VERSION, model);
        final ListAppVersionResourceMappingsResponse listAppVersionResourceMappingsResponse = ListAppVersionResourceMappingsResponse
            .builder()
            .resourceMappings(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING)
            .build();

        when(proxyClient.injectCredentialsAndInvokeV2(describeAppRequest, proxyClient.client()::describeApp))
            .thenReturn(describeAppResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(listTagsForResourceRequest, proxyClient.client()::listTagsForResource))
            .thenReturn(listTagsForResourceResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(describeAppVersionTemplateRequest, proxyClient.client()::describeAppVersionTemplate))
            .thenReturn(describeAppVersionTemplateResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(listResourceMappingsRequest, proxyClient.client()::listAppVersionResourceMappings))
            .thenReturn(listAppVersionResourceMappingsResponse);

        final CallbackContext context = new CallbackContext();
        context.setCreated(true);
        assertThat(handler.handleRequest(proxy, request, context, proxyClient, logger))
            .isEqualTo(ProgressEvent.defaultSuccessHandler(model));

        //App was already created and context.isCreated=true
        verify(proxyClient.client(), never()).createApp(any(CreateAppRequest.class));
    }
}
