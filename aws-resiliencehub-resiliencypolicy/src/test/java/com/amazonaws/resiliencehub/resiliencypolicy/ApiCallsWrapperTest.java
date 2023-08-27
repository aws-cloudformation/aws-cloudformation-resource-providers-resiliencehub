package com.amazonaws.resiliencehub.resiliencypolicy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiCallsWrapperTest {

    @Mock
    private ResiliencehubClient resiliencehubClient;

    @Mock
    private ProxyClient<ResiliencehubClient> proxyClient;

    @BeforeEach
    public void setup() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
    }

    @Test
    public void testCreatePolicy() {
        final CreateResiliencyPolicyRequest createResiliencyPolicyRequest = CreateResiliencyPolicyRequest.builder().build();
        final CreateResiliencyPolicyResponse createResiliencyPolicyResponse = CreateResiliencyPolicyResponse.builder().build();

        when(proxyClient.injectCredentialsAndInvokeV2(same(createResiliencyPolicyRequest), any()))
            .thenReturn(createResiliencyPolicyResponse);

        assertEquals(createResiliencyPolicyResponse, ApiCallsWrapper.createResiliencyPolicy(createResiliencyPolicyRequest, proxyClient));
    }

    @Test
    public void testDeletePolicy() {
        final DeleteResiliencyPolicyRequest deleteResiliencyPolicyRequest = DeleteResiliencyPolicyRequest.builder().build();
        final DeleteResiliencyPolicyResponse deleteResiliencyPolicyResponse = DeleteResiliencyPolicyResponse.builder().build();

        when(proxyClient.injectCredentialsAndInvokeV2(same(deleteResiliencyPolicyRequest), any()))
            .thenReturn(deleteResiliencyPolicyResponse);

        assertEquals(deleteResiliencyPolicyResponse, ApiCallsWrapper.deleteResiliencyPolicy(deleteResiliencyPolicyRequest, proxyClient));
    }

    @Test
    public void testDescribePolicy() {
        final DescribeResiliencyPolicyRequest describeResiliencyPolicyRequest = DescribeResiliencyPolicyRequest.builder().build();
        final DescribeResiliencyPolicyResponse describeResiliencyPolicyResponse = DescribeResiliencyPolicyResponse.builder().build();

        when(proxyClient.injectCredentialsAndInvokeV2(same(describeResiliencyPolicyRequest), any()))
            .thenReturn(describeResiliencyPolicyResponse);

        assertEquals(describeResiliencyPolicyResponse,
            ApiCallsWrapper.describeResiliencyPolicy(describeResiliencyPolicyRequest, proxyClient));
    }

    @Test
    public void testListPolicies() {
        final ListResiliencyPoliciesRequest listResiliencyPoliciesRequest = ListResiliencyPoliciesRequest.builder().build();
        final ListResiliencyPoliciesResponse listResiliencyPoliciesResponse = ListResiliencyPoliciesResponse.builder().build();

        when(proxyClient.injectCredentialsAndInvokeV2(same(listResiliencyPoliciesRequest), any()))
            .thenReturn(listResiliencyPoliciesResponse);

        assertEquals(listResiliencyPoliciesResponse, ApiCallsWrapper.listResiliencyPolicies(listResiliencyPoliciesRequest, proxyClient));
    }

    @Test
    public void testUpdatePolicy() {
        final UpdateResiliencyPolicyRequest updateResiliencyPolicyRequest = UpdateResiliencyPolicyRequest.builder().build();
        final UpdateResiliencyPolicyResponse updateResiliencyPolicyResponse = UpdateResiliencyPolicyResponse.builder().build();

        when(proxyClient.injectCredentialsAndInvokeV2(same(updateResiliencyPolicyRequest), any()))
            .thenReturn(updateResiliencyPolicyResponse);

        assertEquals(updateResiliencyPolicyResponse, ApiCallsWrapper.updateResiliencyPolicy(updateResiliencyPolicyRequest, proxyClient));
    }
}
