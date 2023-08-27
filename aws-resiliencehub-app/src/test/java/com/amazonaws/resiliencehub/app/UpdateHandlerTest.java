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
import software.amazon.awssdk.services.resiliencehub.model.App;
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
import software.amazon.awssdk.services.resiliencehub.model.UpdateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.UpdateAppResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    private ResiliencehubClient sdkClient;

    private AmazonWebServicesClientProxy proxy;
    private ProxyClient<ResiliencehubClient> proxyClient;
    private UpdateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, sdkClient);
        handler = new UpdateHandler();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final App app = TestDataProvider.app();
        final ResourceModel desiredModel = TestDataProvider.resourceModel(app);
        desiredModel.setAppTemplateBody(TestDataProvider.APP_TEMPLATE);
        desiredModel.setResourceMappings(
            Translator.toCfnResourceMappings(ImmutableSet.of(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING)));
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(desiredModel)
            .build();

        final UpdateAppRequest updateAppRequest = Translator.translateToUpdateAppRequest(desiredModel);
        final UpdateAppResponse updateAppResponse = UpdateAppResponse.builder()
            .app(app)
            .build();
        final PutDraftAppVersionTemplateRequest appVersionTemplateRequest = PutDraftAppVersionTemplateRequest.builder()
            .appArn(app.appArn())
            .appTemplateBody(TestDataProvider.APP_TEMPLATE)
            .build();
        final PutDraftAppVersionTemplateResponse appVersionTemplateResponse = PutDraftAppVersionTemplateResponse
            .builder()
            .appArn(app.appArn())
            .build();
        final ListAppVersionResourceMappingsRequest listResourceMappingsRequestForUpdate = Translator
            .translateToListAppVersionResourceMappingsRequest(Constants.DRAFT_VERSION, desiredModel);
        final ListAppVersionResourceMappingsResponse listAppVersionResourceMappingsResponseForUpdate =
            ListAppVersionResourceMappingsResponse
            .builder()
            .resourceMappings(TestDataProvider.NATIVE_SDK_RESOURCE_MAPPING)
            .build();
        final PublishAppVersionRequest publishAppVersionRequest = PublishAppVersionRequest.builder()
            .appArn(app.appArn())
            .build();
        final PublishAppVersionResponse publishAppVersionResponse = PublishAppVersionResponse.builder()
            .appArn(app.appArn())
            .appVersion(Constants.RELEASE_VERSION)
            .build();


        when(proxyClient.injectCredentialsAndInvokeV2(updateAppRequest, proxyClient.client()::updateApp)).thenReturn(updateAppResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(appVersionTemplateRequest, proxyClient.client()::putDraftAppVersionTemplate))
            .thenReturn(appVersionTemplateResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(listResourceMappingsRequestForUpdate,
            proxyClient.client()::listAppVersionResourceMappings)).thenReturn(listAppVersionResourceMappingsResponseForUpdate);
        when(proxyClient.injectCredentialsAndInvokeV2(publishAppVersionRequest, proxyClient.client()::publishAppVersion))
            .thenReturn(publishAppVersionResponse);

        // Read handler invoked in the Update handler at the end
        final DescribeAppRequest describeAppRequest = Translator.translateToReadAppRequest(desiredModel);
        final DescribeAppResponse describeAppResponse = TestDataProvider.describeAppResponse(app);
        final ListTagsForResourceRequest listTagsForResourceRequest = Translator.translateToListTagsForResourceRequest(desiredModel);
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder()
            .tags(app.tags()).build();
        final DescribeAppVersionTemplateRequest describeAppVersionTemplateRequest = Translator
            .translateToDescribeAppVersionTemplateRequest(desiredModel);
        final DescribeAppVersionTemplateResponse describeAppVersionTemplateResponse = DescribeAppVersionTemplateResponse
            .builder()
            .appArn(app.appArn())
            .appVersion(Constants.RELEASE_VERSION)
            .appTemplateBody(TestDataProvider.APP_TEMPLATE)
            .build();
        final ListAppVersionResourceMappingsRequest listResourceMappingsRequestForRead = Translator
            .translateToListAppVersionResourceMappingsRequest(Constants.RELEASE_VERSION, desiredModel);
        final ListAppVersionResourceMappingsResponse listAppVersionResourceMappingsResponseForRead =
            ListAppVersionResourceMappingsResponse
                .builder()
                .resourceMappings(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING)
                .build();

        when(proxyClient.injectCredentialsAndInvokeV2(describeAppRequest, proxyClient.client()::describeApp)).thenReturn(describeAppResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(listTagsForResourceRequest, proxyClient.client()::listTagsForResource))
            .thenReturn(listTagsForResourceResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(describeAppVersionTemplateRequest, proxyClient.client()::describeAppVersionTemplate)).thenReturn(describeAppVersionTemplateResponse);
        when(proxyClient.injectCredentialsAndInvokeV2(listResourceMappingsRequestForRead,
            proxyClient.client()::listAppVersionResourceMappings)).thenReturn(listAppVersionResourceMappingsResponseForRead);

        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.defaultSuccessHandler(desiredModel));
    }
}
