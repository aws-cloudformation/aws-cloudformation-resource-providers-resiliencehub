package com.amazonaws.resiliencehub.resiliencypolicy;

import org.apache.commons.lang3.Validate;

import com.amazonaws.resiliencehub.common.ExceptionHandlerWrapper;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.CreateResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.CreateResiliencyPolicyResponse;
import software.amazon.awssdk.services.resiliencehub.model.DeleteResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.DeleteResiliencyPolicyResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeResiliencyPolicyResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListResiliencyPoliciesRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListResiliencyPoliciesResponse;
import software.amazon.awssdk.services.resiliencehub.model.UpdateResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.UpdateResiliencyPolicyResponse;
import software.amazon.cloudformation.proxy.ProxyClient;

/**
 * Wrapper class for calling AWS::ResilienceHub::ResiliencyPolicy related CRUDL APIs.
 */
public class ApiCallsWrapper {

    private static final String CREATE_RESILIENCY_POLICY = "CreateResiliencyPolicy";
    private static final String DELETE_RESILIENCY_POLICY = "DeleteResiliencyPolicy";
    private static final String DESCRIBE_RESILIENCY_POLICY = "DescribeResiliencyPolicy";
    private static final String LIST_RESILIENCY_POLICIES = "ListResiliencyPolicies";
    private static final String UPDATE_RESILIENCY_POLICY = "UpdateResiliencyPolicy";

    public static CreateResiliencyPolicyResponse createResiliencyPolicy(
        final CreateResiliencyPolicyRequest createResiliencyPolicyRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(createResiliencyPolicyRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(CREATE_RESILIENCY_POLICY,
            () -> proxyClient.injectCredentialsAndInvokeV2(createResiliencyPolicyRequest,
                proxyClient.client()::createResiliencyPolicy));
    }

    public static DeleteResiliencyPolicyResponse deleteResiliencyPolicy(
        final DeleteResiliencyPolicyRequest deleteResiliencyPolicyRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(deleteResiliencyPolicyRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(DELETE_RESILIENCY_POLICY,
            () -> proxyClient.injectCredentialsAndInvokeV2(deleteResiliencyPolicyRequest,
                proxyClient.client()::deleteResiliencyPolicy));
    }

    public static DescribeResiliencyPolicyResponse describeResiliencyPolicy(
        final DescribeResiliencyPolicyRequest describeResiliencyPolicyRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(describeResiliencyPolicyRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(DESCRIBE_RESILIENCY_POLICY,
            () -> proxyClient.injectCredentialsAndInvokeV2(describeResiliencyPolicyRequest,
                proxyClient.client()::describeResiliencyPolicy));
    }

    public static ListResiliencyPoliciesResponse listResiliencyPolicies(
        final ListResiliencyPoliciesRequest listResiliencyPoliciesRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(listResiliencyPoliciesRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(LIST_RESILIENCY_POLICIES,
            () -> proxyClient.injectCredentialsAndInvokeV2(listResiliencyPoliciesRequest,
                proxyClient.client()::listResiliencyPolicies));
    }

    public static UpdateResiliencyPolicyResponse updateResiliencyPolicy(
        final UpdateResiliencyPolicyRequest updateResiliencyPolicyRequest,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(updateResiliencyPolicyRequest);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(UPDATE_RESILIENCY_POLICY,
            () -> proxyClient.injectCredentialsAndInvokeV2(updateResiliencyPolicyRequest,
                proxyClient.client()::updateResiliencyPolicy));
    }

}
