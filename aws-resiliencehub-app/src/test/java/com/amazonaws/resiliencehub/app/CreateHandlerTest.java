package com.amazonaws.resiliencehub.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import com.amazonaws.resiliencehub.common.AbstractTestBase;
import com.amazonaws.resiliencehub.common.Constants;
import com.amazonaws.resiliencehub.common.TaggingUtil;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private ResiliencehubClient sdkClient;

    @Mock
    private ApiCallsWrapper apiCallsWrapper;

    @Mock
    private TaggingUtil taggingUtil;

    private AmazonWebServicesClientProxy proxy;
    private ProxyClient<ResiliencehubClient> proxyClient;
    private CreateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, sdkClient);
        handler = new CreateHandler(apiCallsWrapper, taggingUtil);
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

        doReturn(createAppResponse).when(apiCallsWrapper).createApp(eq(createAppRequest), eq(proxyClient));

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

        doReturn(appVersionTemplateResponse).when(apiCallsWrapper)
            .putDraftAppVersionTemplate(eq(appVersionTemplateRequest), eq(proxyClient));
        doReturn(addResourceMappingsResponse).when(apiCallsWrapper)
            .addDraftAppVersionResourceMappings(eq(addResourceMappingsRequest), eq(proxyClient));
        doReturn(publishAppVersionResponse).when(apiCallsWrapper)
            .publishAppVersion(eq(publishAppVersionRequest), eq(proxyClient));

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

        when(apiCallsWrapper.describeApp(eq(describeAppRequest), eq(proxyClient))).thenReturn(describeAppResponse);
        when(taggingUtil.listTagsForResource(eq(listTagsForResourceRequest), eq(proxyClient))).thenReturn(listTagsForResourceResponse);
        when(apiCallsWrapper.describeAppVersionTemplate(eq(describeAppVersionTemplateRequest), eq(proxyClient)))
            .thenReturn(describeAppVersionTemplateResponse);
        when(apiCallsWrapper.fetchAllResourceMappings(eq(listResourceMappingsRequest), eq(proxyClient)))
            .thenReturn(ImmutableSet.of(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING));

        final CallbackContext context = new CallbackContext();
        context.setCreated(true);
        assertThat(handler.handleRequest(proxy, request, context, proxyClient, logger))
            .isEqualTo(ProgressEvent.defaultSuccessHandler(model));

        //App was already created and context.isCreated=true
        verify(apiCallsWrapper, never()).createApp(any(CreateAppRequest.class), any());
    }
}
