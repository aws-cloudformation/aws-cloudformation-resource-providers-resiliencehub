package com.amazonaws.resiliencehub.resiliencypolicy;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.services.resiliencehub.model.CreateResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.DeleteResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeResiliencyPolicyRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeResiliencyPolicyResponse;
import software.amazon.awssdk.services.resiliencehub.model.DisruptionType;
import software.amazon.awssdk.services.resiliencehub.model.ListResiliencyPoliciesRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListResiliencyPoliciesResponse;
import software.amazon.awssdk.services.resiliencehub.model.UpdateResiliencyPolicyRequest;

/**
 * This class is a centralized placeholder for
 * - api request construction
 * - object translation to/from aws sdk
 * - resource model construction for read/list handlers
 */

public class Translator {

    /**
     * Request to create a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to create a resource
     */
    static CreateResiliencyPolicyRequest translateToCreateRequest(final ResourceModel model) {
        return CreateResiliencyPolicyRequest.builder()
            .policyName(model.getPolicyName())
            .policyDescription(model.getPolicyDescription())
            .dataLocationConstraint(model.getDataLocationConstraint())
            .tier(model.getTier())
            .policy(fromResourceModel(model.getPolicy()))
            .tags(model.getTags())
            .build();
    }

    /**
     * Request to read a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to describe a resource
     */
    static DescribeResiliencyPolicyRequest translateToReadRequest(final ResourceModel model) {
        return DescribeResiliencyPolicyRequest.builder()
            .policyArn(model.getPolicyArn())
            .build();
    }

    /**
     * Translates resource object from sdk into a resource model
     *
     * @param awsResponse the aws service describe resource response
     * @return model resource model
     */
    static ResourceModel translateFromReadResponse(final DescribeResiliencyPolicyResponse awsResponse) {
        // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L58-L73
        return ResourceModel.builder()
            .policyName(awsResponse.policy().policyName())
            .policyDescription(awsResponse.policy().policyDescription())
            .policyArn(awsResponse.policy().policyArn())
            .dataLocationConstraint(awsResponse.policy().dataLocationConstraintAsString())
            .tier(awsResponse.policy().tierAsString())
            .policy(toResourceModel(awsResponse.policy().policy()))
            .tags(awsResponse.policy().tags())
            .build();
    }

    /**
     * Request to delete a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to delete a resource
     */
    static DeleteResiliencyPolicyRequest translateToDeleteRequest(final ResourceModel model) {
        return DeleteResiliencyPolicyRequest.builder()
            .policyArn(model.getPolicyArn())
            .build();
    }

    /**
     * Request to update properties of a previously created resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to modify a resource
     */
    static UpdateResiliencyPolicyRequest translateToUpdateRequest(final ResourceModel model) {
        return UpdateResiliencyPolicyRequest.builder()
            .policyArn(model.getPolicyArn())
            .policyName(model.getPolicyName())
            .policyDescription(model.getPolicyDescription())
            .dataLocationConstraint(model.getDataLocationConstraint())
            .tier(model.getTier())
            .policy(fromResourceModel(model.getPolicy()))
            .build();
    }

    /**
     * Request to list resources
     *
     * @param nextToken token passed to the aws service list resources request
     * @return awsRequest the aws service request to list resources within aws account
     */
    static ListResiliencyPoliciesRequest translateToListRequest(final String nextToken) {
        return ListResiliencyPoliciesRequest.builder()
            .nextToken(nextToken)
            .build();
    }

    /**
     * Translates resource objects from sdk into a resource model (primary identifier only)
     *
     * @param awsResponse the aws service describe resource response
     * @return list of resource models
     */
    static List<ResourceModel> translateFromListResponse(final ListResiliencyPoliciesResponse awsResponse) {
        // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L75-L82
        return streamOfOrEmpty(awsResponse.resiliencyPolicies())
            .map(resource -> ResourceModel.builder()
                // include only primary identifier
                .policyArn(resource.policyArn())
                .build())
            .collect(Collectors.toList());
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
    }

    static Map<DisruptionType, software.amazon.awssdk.services.resiliencehub.model.FailurePolicy> fromResourceModel(
        final Map<String, FailurePolicy> policy) {
        final Map<DisruptionType, software.amazon.awssdk.services.resiliencehub.model.FailurePolicy> policyMap = new HashMap<>();

        for (final Entry<String, FailurePolicy>  entry : policy.entrySet()) {
            policyMap.put(DisruptionType.fromValue(entry.getKey()), software.amazon.awssdk.services.resiliencehub.model.FailurePolicy.builder()
                .rpoInSecs(entry.getValue().getRpoInSecs())
                .rtoInSecs(entry.getValue().getRtoInSecs())
                .build());
        }
        return policyMap;
    }

    static Map<String, FailurePolicy> toResourceModel(
        final Map<DisruptionType, software.amazon.awssdk.services.resiliencehub.model.FailurePolicy> policy) {
        final Map<String, FailurePolicy> policyMap = new HashMap<>();
        for (final Entry<DisruptionType, software.amazon.awssdk.services.resiliencehub.model.FailurePolicy> entry : policy.entrySet()) {
            policyMap.put(entry.getKey().toString(), FailurePolicy.builder()
                .rpoInSecs(entry.getValue().rpoInSecs())
                .rtoInSecs(entry.getValue().rtoInSecs())
                .build());
        }
        return policyMap;
    }
}
