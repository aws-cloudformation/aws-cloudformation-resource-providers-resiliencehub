package com.amazonaws.resiliencehub.app;

import org.apache.commons.lang3.Validate;

import com.amazonaws.resiliencehub.common.TaggingUtil;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class CreateHandler extends BaseHandlerStd {

    static final int CALLBACK_DELAY_SECONDS = 1;

    private Logger logger;
    private final TaggingUtil taggingUtil;

    public CreateHandler() {
        super();
        this.taggingUtil = new TaggingUtil();
    }

    public CreateHandler(final ApiCallsWrapper apiCallsWrapper, final TaggingUtil taggingUtil) {
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
            .then(progress -> createApp(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> addTemplate(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> addResourceMappings(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> publishVersion(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            // Describe call/chain to return the resource model
            .then(progress -> new ReadHandler(apiCallsWrapper, taggingUtil)
                .handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private ProgressEvent<ResourceModel, CallbackContext> createApp(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        logger.log(String.format("isCreatedFlag: %s", callbackContext.isCreated()));
        if (callbackContext.isCreated()) {
            logger.log("AWS::ResilienceHub::App was already created, will not call CreateApp again.");
            return ProgressEvent.progress(model, callbackContext);
        }
        return proxy.initiate("AWS-ResilienceHub-App::create-app", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToCreateAppRequest)
            .makeServiceCall(apiCallsWrapper::createApp)
            .done(createAppResponse -> {
                model.setAppArn(createAppResponse.app().appArn());
                callbackContext.setCreated(true);
                logger.log(String.format("Successfully created app [%s] for resourceType %s. Continuing further..", model.getName(),
                    ResourceModel.TYPE_NAME));
                return ProgressEvent.defaultInProgressHandler(callbackContext, CALLBACK_DELAY_SECONDS, model);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> addTemplate(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::add-template", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToPutDraftAppVersionTemplateRequest)
            .makeServiceCall(apiCallsWrapper::putDraftAppVersionTemplate)
            .done(putDraftAppVersionTemplateResponse -> {
                logger.log(String.format("Successfully added template to %s [%s].",
                    ResourceModel.TYPE_NAME, model.getName()));
                return ProgressEvent.progress(model, callbackContext);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> addResourceMappings(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::add-resource-mappings", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToAddDraftAppVersionResourceMappingsRequest)
            .makeServiceCall(apiCallsWrapper::addDraftAppVersionResourceMappings)
            .done(addDraftAppVersionResourceMappingsResponse -> {
                logger.log(String.format("Successfully added resource mappings to %s [%s].",
                    ResourceModel.TYPE_NAME, model.getName()));
                return ProgressEvent.progress(model, callbackContext);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> publishVersion(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::publish-version", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToPublishAppVersionRequest)
            .makeServiceCall(apiCallsWrapper::publishAppVersion)
            .done(publishAppVersionResponse -> {
                logger.log(String
                    .format("Successfully published version [%s] for app [%s]. This completes the CREATE for resource type %s.",
                        publishAppVersionResponse.appVersion(), model.getName(), ResourceModel.TYPE_NAME));
                return ProgressEvent.progress(model, callbackContext);
            });
    }
}
