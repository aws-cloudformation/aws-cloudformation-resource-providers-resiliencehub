package com.amazonaws.resiliencehub.app;

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
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final Logger logger) {

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress ->
                proxy.initiate("AWS-ResilienceHub-App::list-apps", proxyClient, progress.getResourceModel(),
                    progress.getCallbackContext())
                    .translateToServiceRequest(
                        model -> Translator.translateToListAppRequest(request.getNextToken()))
                    .makeServiceCall(ApiCallsWrapper::listApps)
                    .done(listAppsResponse -> ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModels(Translator.translateFromListResponse(listAppsResponse))
                        .status(OperationStatus.SUCCESS)
                        .nextToken(listAppsResponse.nextToken())
                        .build()));
    }
}
