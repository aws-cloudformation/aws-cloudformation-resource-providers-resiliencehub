package com.amazonaws.resiliencehub.app;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.DeleteAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DeleteAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {

    public DeleteHandler() {
        super();
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final Logger logger) {

        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            // Trigger the deletion and wait for stabilize
            .then(progress ->
                proxy.initiate("AWS-ResilienceHub-App::Delete", proxyClient, progress.getResourceModel(),
                    progress.getCallbackContext())
                    .translateToServiceRequest(Translator::translateToDeleteAppRequest)
                    .makeServiceCall(ApiCallsWrapper::deleteApp)
                    .stabilize(this::stabilizeOnDelete)
                    .done(deleteAppResponse -> {
                        logger.log(String.format("%s [%s] successfully deleted.", ResourceModel.TYPE_NAME,
                            progress.getResourceModel().getName()));
                        return progress;
                    }))
            // return the successful progress event without resource model
            .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private boolean stabilizeOnDelete(
        final DeleteAppRequest deleteAppRequest,
        final DeleteAppResponse deleteAppResponse,
        final ProxyClient<ResiliencehubClient> proxyClient,
        final ResourceModel model,
        final CallbackContext context) {
        final ListAppsRequest listAppsRequest = Translator
            .translateToListRequestWithAppArnFilter(model.getAppArn());
        final ListAppsResponse listAppsResponse = ApiCallsWrapper.listApps(listAppsRequest, proxyClient);

        return listAppsResponse.appSummaries().isEmpty();
    }
}
