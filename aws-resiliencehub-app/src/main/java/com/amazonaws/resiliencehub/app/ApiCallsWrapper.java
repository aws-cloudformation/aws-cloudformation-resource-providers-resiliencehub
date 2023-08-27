package com.amazonaws.resiliencehub.app;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Set;

import com.amazonaws.resiliencehub.common.ExceptionHandlerWrapper;
import com.google.common.collect.Sets;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.AddDraftAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.AddDraftAppVersionResourceMappingsResponse;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DeleteAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DeleteAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppVersionTemplateRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppVersionTemplateResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppVersionResourceMappingsResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsResponse;
import software.amazon.awssdk.services.resiliencehub.model.PublishAppVersionRequest;
import software.amazon.awssdk.services.resiliencehub.model.PublishAppVersionResponse;
import software.amazon.awssdk.services.resiliencehub.model.PutDraftAppVersionTemplateRequest;
import software.amazon.awssdk.services.resiliencehub.model.PutDraftAppVersionTemplateResponse;
import software.amazon.awssdk.services.resiliencehub.model.RemoveDraftAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ResourceMapping;
import software.amazon.awssdk.services.resiliencehub.model.UpdateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.UpdateAppResponse;
import software.amazon.cloudformation.proxy.ProxyClient;

/**
 * Wrapper class for calling AWS::ResilienceHub::App related CRUDL APIs.
 */
public class ApiCallsWrapper {

    private static final String CREATE_APP = "CreateApp";
    private static final String DESCRIBE_APP = "DescribeApp";
    private static final String UPDATE_APP = "UpdateApp";
    private static final String DELETE_APP = "DeleteApp";
    private static final String LIST_APPS = "ListApps";
    private static final String PUT_DRAFT_APP_VERSION_TEMPLATE = "PutDraftAppVersionTemplate";
    private static final String ADD_DRAFT_APP_VERSION_RESOURCE_MAPPINGS = "AddDraftAppVersionResourceMappings";
    private static final String REMOVE_DRAFT_APP_VERSION_RESOURCE_MAPPINGS = "RemoveDraftAppVersionResourceMappings";
    private static final String PUBLISH_APP_VERSION = "PublishAppVersion";
    private static final String DESCRIBE_APP_VERSION_TEMPLATE = "DescribeAppVersionTemplate";
    private static final String LIST_APP_VERSION_RESOURCE_MAPPINGS = "ListAppVersionResourceMappings";

    public static CreateAppResponse createApp(
        final CreateAppRequest createAppRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(createAppRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(CREATE_APP,
            () -> proxyClient.injectCredentialsAndInvokeV2(createAppRequest,
                proxyClient.client()::createApp));
    }

    public static DescribeAppResponse describeApp(
        final DescribeAppRequest describeAppRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(describeAppRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(DESCRIBE_APP,
            () -> proxyClient.injectCredentialsAndInvokeV2(describeAppRequest,
                proxyClient.client()::describeApp));
    }

    public static UpdateAppResponse updateApp(
        final UpdateAppRequest updateAppRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(updateAppRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(UPDATE_APP,
            () -> proxyClient.injectCredentialsAndInvokeV2(updateAppRequest,
                proxyClient.client()::updateApp));
    }

    public static DeleteAppResponse deleteApp(
        final DeleteAppRequest deleteAppRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(deleteAppRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(DELETE_APP,
            () -> proxyClient.injectCredentialsAndInvokeV2(deleteAppRequest,
                proxyClient.client()::deleteApp));
    }

    public static ListAppsResponse listApps(
        final ListAppsRequest listAppsRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(listAppsRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(LIST_APPS,
            () -> proxyClient.injectCredentialsAndInvokeV2(listAppsRequest,
                proxyClient.client()::listApps));
    }

    public static PutDraftAppVersionTemplateResponse putDraftAppVersionTemplate(
        final PutDraftAppVersionTemplateRequest putDraftAppVersionTemplateRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(putDraftAppVersionTemplateRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(PUT_DRAFT_APP_VERSION_TEMPLATE,
            () -> proxyClient.injectCredentialsAndInvokeV2(putDraftAppVersionTemplateRequest,
                proxyClient.client()::putDraftAppVersionTemplate));
    }

    public static AddDraftAppVersionResourceMappingsResponse addDraftAppVersionResourceMappings(
        final AddDraftAppVersionResourceMappingsRequest addDraftAppVersionResourceMappingsRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(addDraftAppVersionResourceMappingsRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(ADD_DRAFT_APP_VERSION_RESOURCE_MAPPINGS,
            () -> proxyClient.injectCredentialsAndInvokeV2(addDraftAppVersionResourceMappingsRequest,
                proxyClient.client()::addDraftAppVersionResourceMappings));
    }

    public static void addDraftAppVersionResourceMappings(
        final String appArn,
        final Set<ResourceMapping> resourceMappings,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notBlank(appArn);
        Validate.notNull(resourceMappings);
        Validate.notNull(proxyClient);

        if (CollectionUtils.isNotEmpty(resourceMappings)) {
            final AddDraftAppVersionResourceMappingsRequest request = Translator
                .translateToAddDraftAppVersionResourceMappingsRequest(appArn, resourceMappings);

            addDraftAppVersionResourceMappings(request, proxyClient);
        }
    }

    public static void removeDraftAppVersionResourceMappings(
        final String appArn,
        final Set<ResourceMapping> resourceMappings,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notBlank(appArn);
        Validate.notNull(resourceMappings);
        Validate.notNull(proxyClient);

        if (CollectionUtils.isNotEmpty(resourceMappings)) {
            final RemoveDraftAppVersionResourceMappingsRequest request = Translator
                .translateToRemoveDraftAppVersionResourceMappingsRequest(appArn, resourceMappings);

            ExceptionHandlerWrapper.wrapResilienceHubExceptions(REMOVE_DRAFT_APP_VERSION_RESOURCE_MAPPINGS,
                () -> proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::removeDraftAppVersionResourceMappings));
        }
    }

    public static PublishAppVersionResponse publishAppVersion(
        final PublishAppVersionRequest publishAppVersionRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(publishAppVersionRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(PUBLISH_APP_VERSION,
            () -> proxyClient.injectCredentialsAndInvokeV2(publishAppVersionRequest,
                proxyClient.client()::publishAppVersion));
    }

    public static DescribeAppVersionTemplateResponse describeAppVersionTemplate(
        final DescribeAppVersionTemplateRequest describeAppVersionTemplateRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(describeAppVersionTemplateRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(DESCRIBE_APP_VERSION_TEMPLATE,
            () -> proxyClient.injectCredentialsAndInvokeV2(describeAppVersionTemplateRequest,
                proxyClient.client()::describeAppVersionTemplate));
    }

    public static Set<ResourceMapping> fetchAllResourceMappings(
        final ListAppVersionResourceMappingsRequest listAppVersionResourceMappingsRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(listAppVersionResourceMappingsRequest);
        Validate.notNull(proxyClient);

        final Set<ResourceMapping> resourceMappings = Sets.newHashSet();
        final ListAppVersionResourceMappingsRequest.Builder requestBuilder = listAppVersionResourceMappingsRequest
            .toBuilder();
        String nextToken = null;
        do {
            requestBuilder.nextToken(nextToken);
            final ListAppVersionResourceMappingsResponse response = ExceptionHandlerWrapper
                .wrapResilienceHubExceptions(LIST_APP_VERSION_RESOURCE_MAPPINGS,
                    () -> proxyClient.injectCredentialsAndInvokeV2(requestBuilder.build(),
                        proxyClient.client()::listAppVersionResourceMappings));
            nextToken = response.nextToken();
            resourceMappings.addAll(response.resourceMappings());
        } while (StringUtils.isNotEmpty(nextToken));
        return resourceMappings;
    }

}
