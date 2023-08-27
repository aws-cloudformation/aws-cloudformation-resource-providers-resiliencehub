package com.amazonaws.resiliencehub.resiliencypolicy;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class CreateHandler extends BaseHandlerStd {

    static final int CALLBACK_DELAY_SECONDS = 1;

    private Logger logger;

    public CreateHandler() {
        super();
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
            .then(progress -> createPolicy(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            // Describe call/chain to return the resource model
            .then(progress -> new ReadHandler()
                .handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private ProgressEvent<ResourceModel, CallbackContext> createPolicy(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        logger.log(String.format("isCreatedFlag: %s", callbackContext.isCreated()));
        if (callbackContext.isCreated()) {
            logger.log("AWS::ResilienceHub::ResiliencyPolicy was already created, will not call CreateResiliencyPolicy again.");
            return ProgressEvent.progress(model, callbackContext);
        }
        return proxy.initiate("AWS-ResilienceHub-ResiliencyPolicy::Create", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToCreateRequest)
            .makeServiceCall(ApiCallsWrapper::createResiliencyPolicy)
            .done(createResiliencyPolicyResponse -> {
                model.setPolicyArn(createResiliencyPolicyResponse.policy().policyArn());
                callbackContext.setCreated(true);
                logger.log(String.format("%s [%s] successfully created.", ResourceModel.TYPE_NAME, model.getPolicyName()));
                return ProgressEvent.defaultInProgressHandler(callbackContext, CALLBACK_DELAY_SECONDS, model);
            });
    }
}
