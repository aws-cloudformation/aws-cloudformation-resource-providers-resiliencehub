package com.amazonaws.resiliencehub.resiliencypolicy;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {

    private Logger logger;

    public DeleteHandler() {
        super();
    }

    public DeleteHandler(final ApiCallsWrapper apiCallsWrapper) {
        super(apiCallsWrapper);
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
            .then(progress ->
                proxy.initiate("AWS-ResilienceHub-ResiliencyPolicy::Delete", proxyClient, progress.getResourceModel(),
                    progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToDeleteRequest)
                    .makeServiceCall(apiCallsWrapper::deleteResiliencyPolicy)
                    .done(deleteResiliencyPolicyResponse -> {
                        logger.log(String.format("%s [%s] successfully deleted.", ResourceModel.TYPE_NAME,
                            progress.getResourceModel().getPolicyName()));
                        return progress;
                    })
            )
            // Return the successful progress event without resource model
            .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }
}
