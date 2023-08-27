package com.amazonaws.resiliencehub.resiliencypolicy;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {

    private Logger logger;

    public ReadHandler() {
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

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress -> readPolicy(proxy, proxyClient, progress.getCallbackContext(), progress.getResourceModel()))
            .then(progress -> ProgressEvent.defaultSuccessHandler(progress.getResourceModel()));
    }

    private ProgressEvent<ResourceModel, CallbackContext> readPolicy(
        final AmazonWebServicesClientProxy proxy,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final CallbackContext callbackContext,
        final ResourceModel model) {
        return proxy.initiate("AWS-ResilienceHub-ResiliencyPolicy::read-policy", proxyClient, model, callbackContext)
            .translateToServiceRequest(Translator::translateToReadRequest)
            .makeServiceCall(ApiCallsWrapper::describeResiliencyPolicy)
            .done(describeResiliencyPolicyResponse -> {
                final ResourceModel outputModel = Translator.translateFromReadResponse(describeResiliencyPolicyResponse);
                logger.log(String.format("Successfully read resiliency policy [%s] for resourceType %s. Continuing further..",
                    outputModel.getPolicyName(), ResourceModel.TYPE_NAME));
                return ProgressEvent.progress(outputModel, callbackContext);
            });
    }
}
