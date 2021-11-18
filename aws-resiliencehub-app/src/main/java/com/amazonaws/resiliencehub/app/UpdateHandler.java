package com.amazonaws.resiliencehub.app;

import org.apache.commons.lang3.Validate;

import java.util.Set;

import com.amazonaws.resiliencehub.common.Constants;
import com.amazonaws.resiliencehub.common.TaggingUtil;
import com.google.common.collect.Sets;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.ListAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ResourceMapping;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {

    private Logger logger;
    private final TaggingUtil taggingUtil;

    public UpdateHandler() {
        super();
        this.taggingUtil = new TaggingUtil();
    }

    public UpdateHandler(final ApiCallsWrapper apiCallsWrapper, final TaggingUtil taggingUtil) {
        super(apiCallsWrapper);
        Validate.notNull(taggingUtil);

        this.taggingUtil = taggingUtil;
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress -> updateApp(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> updateTemplate(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> updateResourceMappings(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> publishUpdatedVersion(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> updateTags(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> new ReadHandler(apiCallsWrapper, taggingUtil)
                .handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateApp(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::update-app", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToUpdateAppRequest)
            .makeServiceCall(apiCallsWrapper::updateApp)
            .done(updateAppResponse -> {
                logger.log(String.format("%s [%s] successfully Updated.", com.amazonaws.resiliencehub.app.ResourceModel.TYPE_NAME,
                    model.getName()));
                return ProgressEvent.progress(model, callbackContext);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateTemplate(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::update-template", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToPutDraftAppVersionTemplateRequest)
            .makeServiceCall(apiCallsWrapper::putDraftAppVersionTemplate)
            .done(putDraftAppVersionTemplateResponse -> {
                logger.log(String.format("Successfully updated template for %s [%s].",
                    ResourceModel.TYPE_NAME, model.getName()));
                return ProgressEvent.progress(model, callbackContext);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateResourceMappings(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {

        final ListAppVersionResourceMappingsRequest request = Translator
            .translateToListAppVersionResourceMappingsRequest(Constants.DRAFT_VERSION, model);

        final Set<ResourceMapping> existingResourceMappings = apiCallsWrapper.fetchAllResourceMappings(request, proxyClient);
        final Set<ResourceMapping> desiredResourceMappings = Translator.toSdkResourceMappings(model.getResourceMappings());
        final Set<ResourceMapping> resourceMappingsToRemove = Sets.difference(existingResourceMappings, desiredResourceMappings);
        final Set<ResourceMapping> resourceMappingsToAdd = Sets.difference(desiredResourceMappings, existingResourceMappings);

        apiCallsWrapper.removeDraftAppVersionResourceMappings(model.getAppArn(), resourceMappingsToRemove, proxyClient);
        apiCallsWrapper.addDraftAppVersionResourceMappings(model.getAppArn(), resourceMappingsToAdd, proxyClient);
        logger.log(String.format("Successfully updated resource mappings for %s [%s].", ResourceModel.TYPE_NAME, model.getName()));

        return ProgressEvent.progress(model, callbackContext);
    }

    private ProgressEvent<ResourceModel, CallbackContext> publishUpdatedVersion(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::publish-updated-version", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToPublishAppVersionRequest)
            .makeServiceCall(apiCallsWrapper::publishAppVersion)
            .done(publishAppVersionResponse -> {
                logger.log(String
                    .format("Successfully published an updated version [%s] for app %s [%s].",
                        publishAppVersionResponse.appVersion(), ResourceModel.TYPE_NAME, model.getName()));
                return ProgressEvent.progress(model, callbackContext);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateTags(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {

        taggingUtil.updateTags(model.getAppArn(), model.getTags(), proxyClient);
        logger.log(String.format("Successfully updated tags for app [%s]. This completes the UPDATE for resource type %s.", model.getName(),
            ResourceModel.TYPE_NAME));
        return ProgressEvent.progress(model, callbackContext);
    }
}
