package com.amazonaws.resiliencehub.app;

import org.apache.commons.lang3.Validate;

import com.amazonaws.resiliencehub.common.Constants;
import com.amazonaws.resiliencehub.common.TaggingUtil;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {

    private Logger logger;
    private final TaggingUtil taggingUtil;

    public ReadHandler() {
        super();
        this.taggingUtil = new TaggingUtil();
    }

    public ReadHandler(final ApiCallsWrapper apiCallsWrapper, final TaggingUtil taggingUtil) {
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
            .then(progress -> readApp(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> readTags(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> describeAppTemplate(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> listAppResourceMappings(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> ProgressEvent.defaultSuccessHandler(progress.getResourceModel()));
    }

    private ProgressEvent<ResourceModel, CallbackContext> readApp(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::read-app", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToReadAppRequest)
            .makeServiceCall(apiCallsWrapper::describeApp)
            .done(describeAppResponse -> {
                final ResourceModel readModel = Translator.translateFromReadResponse(describeAppResponse);
                logger.log(String.format("Successfully read app [%s] for resource type %s. Continuing further..", readModel.getName(),
                    ResourceModel.TYPE_NAME));
                return ProgressEvent.progress(readModel, callbackContext);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> readTags(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::read-tags", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToListTagsForResourceRequest)
            .makeServiceCall(taggingUtil::listTagsForResource)
            .done(listTagsForResourceResponse -> {
                model.setTags(listTagsForResourceResponse.tags());
                logger.log(String.format("Successfully read tags for app [%s] resource type %s. Continuing further..", model.getName(),
                    ResourceModel.TYPE_NAME));
                return ProgressEvent.progress(model, callbackContext);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> describeAppTemplate(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::describe-app-template", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToDescribeAppVersionTemplateRequest)
            .makeServiceCall(apiCallsWrapper::describeAppVersionTemplate)
            .done(describeAppVersionTemplateResponse -> {
                model.setAppTemplateBody(describeAppVersionTemplateResponse.appTemplateBody());
                logger.log(String.format("App template for %s [%s] has been successfully read.",
                    ResourceModel.TYPE_NAME, model.getName()));
                return ProgressEvent.progress(model, callbackContext);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> listAppResourceMappings(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-App::list-resource-mappings", proxyClient, model, callbackContext)
            .translateToServiceRequest(
                resourceModel -> Translator.translateToListAppVersionResourceMappingsRequest(Constants.RELEASE_VERSION, resourceModel))
            .makeServiceCall(apiCallsWrapper::fetchAllResourceMappings)
            .done(resourceMappings -> {
                model.setResourceMappings(Translator.toCfnResourceMappings(resourceMappings));
                logger.log(String
                    .format("Resource mappings for app [%s] has been successfully read. This completes the READ for resource type %s.",
                        model.getName(), ResourceModel.TYPE_NAME));
                return ProgressEvent.progress(model, callbackContext);
            });
    }
}
