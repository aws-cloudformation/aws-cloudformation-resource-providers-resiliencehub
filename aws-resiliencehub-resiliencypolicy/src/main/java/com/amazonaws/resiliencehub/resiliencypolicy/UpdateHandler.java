package com.amazonaws.resiliencehub.resiliencypolicy;

import org.apache.commons.lang3.Validate;

import com.amazonaws.resiliencehub.common.TaggingUtil;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
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
            .then(progress -> updatePolicy(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> updateTags(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> new ReadHandler(apiCallsWrapper)
                .handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private ProgressEvent<ResourceModel, CallbackContext> updatePolicy(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-ResiliencyPolicy::Update", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToUpdateRequest)
            .makeServiceCall(apiCallsWrapper::updateResiliencyPolicy)
            .done(updateResiliencyPolicyResponse -> {
                model.setPolicyArn(updateResiliencyPolicyResponse.policy().policyArn());
                logger.log(String.format("%s [%s] successfully Updated.", ResourceModel.TYPE_NAME, model.getPolicyName()));
                return ProgressEvent.progress(model, callbackContext);
            });
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateTags(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {

        taggingUtil.updateTags(model.getPolicyArn(), model.getTags(), proxyClient);
        logger.log(String.format("Successfully updated tags for resiliency policy [%s]. This completes the Update for resourceType %s.",
            model.getPolicyName(), ResourceModel.TYPE_NAME));
        return ProgressEvent.progress(model, callbackContext);
    }
}
