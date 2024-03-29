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
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

    @Mock
    private ResiliencehubClient sdkClient;

    private AmazonWebServicesClientProxy proxy;
    private ProxyClient<ResiliencehubClient> proxyClient;
    private ReadHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        proxyClient = MOCK_PROXY(proxy, sdkClient);
        handler = new ReadHandler();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final App app = TestDataProvider.app();
        final ResourceModel model = TestDataProvider.resourceModel(app);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

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
        final ListAppVersionResourceMappingsRequest resourceMappingsRequest = Translator
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
        when(proxyClient.injectCredentialsAndInvokeV2(resourceMappingsRequest, proxyClient.client()::listAppVersionResourceMappings))
            .thenReturn(listAppVersionResourceMappingsResponse);

        model.setAppTemplateBody(TestDataProvider.APP_TEMPLATE);
        model.setResourceMappings(
            Translator.toCfnResourceMappings(ImmutableSet.of(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING)));
        assertThat(handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger))
            .isEqualTo(ProgressEvent.defaultSuccessHandler(model));
    }
}
