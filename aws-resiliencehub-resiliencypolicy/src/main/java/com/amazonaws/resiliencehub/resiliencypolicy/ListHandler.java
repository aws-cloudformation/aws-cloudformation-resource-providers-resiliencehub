package com.amazonaws.resiliencehub.resiliencypolicy;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandlerStd {

    public ListHandler() {
        super();
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final Logger logger) {

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress ->
                proxy.initiate("AWS-ResilienceHub-ResiliencyPolicy::list-policies", proxyClient, progress.getResourceModel(),
                    progress.getCallbackContext())
                    .translateToServiceRequest(model -> Translator.translateToListRequest(request.getNextToken()))
                    .makeServiceCall(ApiCallsWrapper::listResiliencyPolicies)
                    .done(listResiliencyPolicyResponse -> ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModels(Translator.translateFromListResponse(listResiliencyPolicyResponse))
                        .status(OperationStatus.SUCCESS)
                        .nextToken(listResiliencyPolicyResponse.nextToken())
                        .build()));
    }
}
