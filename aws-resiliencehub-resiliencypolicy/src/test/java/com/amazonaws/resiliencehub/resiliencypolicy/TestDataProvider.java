package com.amazonaws.resiliencehub.resiliencypolicy;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import software.amazon.awssdk.services.resiliencehub.model.ResiliencyPolicy;

public class TestDataProvider {

    public static final String RESILIENCY_POLICY_NAME = "policy-name";
    public static final String RESILIENCY_POLICY_DESCRIPTION = "policy-description";
    public static final String RESILIENCY_POLICY_ARN = "policyArn";
    public static final String TIER = "Important";
    public static final String DATA_LOCATION_CONSTRAINT = "AnyLocation";
    public static final String DISRUPTION_TYPE = "AZ";
    public static final String TAG_KEY = "TagKey";
    public static final String TAG_VALUE = "TagValue";
    public static final int RPO_IN_SECONDS = 10;
    public static final int RTO_IN_SECONDS = 10;

    public static ResourceModel getResourceModelWithResiliencyPolicyArn() {
        final ResourceModel resourceModel = getResourceModelWithoutResiliencyPolicyArn();
        resourceModel.setPolicyArn(RESILIENCY_POLICY_ARN);
        return resourceModel;
    }

    public static ResourceModel getResourceModelWithoutResiliencyPolicyArn() {
        return ResourceModel.builder()
            .policyName(RESILIENCY_POLICY_NAME)
            .policyDescription(RESILIENCY_POLICY_DESCRIPTION)
            .tier(TIER)
            .dataLocationConstraint(DATA_LOCATION_CONSTRAINT)
            .policy(getPolicyMap())
            .tags(getTagsMap())
            .build();
    }

    public static ResourceModel getResourceModelWithResiliencyPolicyArn(
        final ResiliencyPolicy resiliencyPolicy) {
        return ResourceModel.builder()
            .policyArn(resiliencyPolicy.policyArn())
            .policyName(resiliencyPolicy.policyName())
            .policyDescription(resiliencyPolicy.policyDescription())
            .tier(resiliencyPolicy.tierAsString())
            .dataLocationConstraint(resiliencyPolicy.dataLocationConstraintAsString())
            .policy(Translator.toResourceModel(resiliencyPolicy.policy()))
            .tags(resiliencyPolicy.tags())
            .build();
    }

    public static software.amazon.awssdk.services.resiliencehub.model.ResiliencyPolicy getResiliencyPolicy() {
        return software.amazon.awssdk.services.resiliencehub.model.ResiliencyPolicy.builder()
            .policyArn(RESILIENCY_POLICY_ARN)
            .policyName(RESILIENCY_POLICY_NAME)
            .policyDescription(RESILIENCY_POLICY_DESCRIPTION)
            .tier(TIER)
            .dataLocationConstraint(DATA_LOCATION_CONSTRAINT)
            .policy(Translator.fromResourceModel(getPolicyMap()))
            .tags(getTagsMap())
            .build();
    }

    public static Map<String, FailurePolicy> getPolicyMap() {
        return new ImmutableMap.Builder<String, FailurePolicy>()
            .put(DISRUPTION_TYPE, FailurePolicy.builder()
                .rpoInSecs(RPO_IN_SECONDS)
                .rtoInSecs(RTO_IN_SECONDS)
                .build())
            .build();
    }

    private static Map<String, String> getTagsMap() {
        return new ImmutableMap.Builder<String, String>()
            .put(TAG_KEY, TAG_VALUE)
            .build();
    }
}
