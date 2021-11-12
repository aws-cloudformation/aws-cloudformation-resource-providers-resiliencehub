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
import software.amazon.awssdk.services.resiliencehub.model.App;
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
import software.amazon.awssdk.services.resiliencehub.model.UpdateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.UpdateAppResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
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
        final PublishAppVersionRequest publishAppVersionRequest = PublishAppVersionRequest.builder()
            .appArn(app.appArn())
            .build();
        final PublishAppVersionResponse publishAppVersionResponse = PublishAppVersionResponse.builder()
            .appArn(app.appArn())
            .appVersion(Constants.RELEASE_VERSION)
            .build();

        doReturn(updateAppResponse).when(apiCallsWrapper).updateApp(eq(updateAppRequest), eq(proxyClient));
        doReturn(appVersionTemplateResponse).when(apiCallsWrapper)
            .putDraftAppVersionTemplate(eq(appVersionTemplateRequest), eq(proxyClient));
        doReturn(ImmutableSet.of(TestDataProvider.NATIVE_SDK_RESOURCE_MAPPING)).when(apiCallsWrapper)
            .fetchAllResourceMappings(eq(listResourceMappingsRequestForUpdate), eq(proxyClient));
        doReturn(publishAppVersionResponse).when(apiCallsWrapper)
            .publishAppVersion(eq(publishAppVersionRequest), eq(proxyClient));

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

        when(apiCallsWrapper.describeApp(eq(describeAppRequest), eq(proxyClient))).thenReturn(describeAppResponse);
        when(taggingUtil.listTagsForResource(eq(listTagsForResourceRequest), eq(proxyClient))).thenReturn(listTagsForResourceResponse);
        when(apiCallsWrapper.describeAppVersionTemplate(eq(describeAppVersionTemplateRequest), eq(proxyClient)))
            .thenReturn(describeAppVersionTemplateResponse);
        when(apiCallsWrapper.fetchAllResourceMappings(eq(listResourceMappingsRequestForRead), eq(proxyClient)))
            .thenReturn(ImmutableSet.of(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING));

        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.defaultSuccessHandler(desiredModel));
    }
}
