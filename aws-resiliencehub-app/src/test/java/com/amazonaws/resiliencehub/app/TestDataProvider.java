package com.amazonaws.resiliencehub.app;

import java.time.Instant;

import com.google.common.collect.ImmutableMap;

import software.amazon.awssdk.services.resiliencehub.model.App;
import software.amazon.awssdk.services.resiliencehub.model.AppAssessmentScheduleType;
import software.amazon.awssdk.services.resiliencehub.model.AppComplianceStatusType;
import software.amazon.awssdk.services.resiliencehub.model.AppStatusType;
import software.amazon.awssdk.services.resiliencehub.model.AppSummary;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.PhysicalIdentifierType;
import software.amazon.awssdk.services.resiliencehub.model.PhysicalResourceId;
import software.amazon.awssdk.services.resiliencehub.model.ResourceMapping;
import software.amazon.awssdk.services.resiliencehub.model.ResourceMappingType;

public class TestDataProvider {

    public static final String APP_ARN = "arn1";
    public static final String APP_NAME = "appName";
    public static final String APP_DESC = "appDesc";
    public static final String APP_TEMPLATE = "appTemplateBody";
    public static final String LOGICAL_STACK_NAME = "logicalStackName";
    public static final String TERRAFORM_SOURCE_NAME = "stateFile.tf";
    public static final String RESOURCE_NAME = "resourceName";
    public static final String POLICY_ARN = "PolicyArn";

    public static final ResourceMapping CFN_BACKED_SDK_RESOURCE_MAPPING = ResourceMapping.builder()
        .logicalStackName(LOGICAL_STACK_NAME)
        .mappingType(ResourceMappingType.CFN_STACK)
        .physicalResourceId(PhysicalResourceId.builder()
            .type(PhysicalIdentifierType.ARN)
            .identifier("Identifier")
            .build())
        .build();
    public static final ResourceMapping NATIVE_SDK_RESOURCE_MAPPING = ResourceMapping.builder()
        .resourceName(RESOURCE_NAME)
        .mappingType(ResourceMappingType.RESOURCE)
        .physicalResourceId(PhysicalResourceId.builder()
            .type(PhysicalIdentifierType.NATIVE)
            .identifier("Identifier")
            .build())
        .build();
    public static final ResourceMapping TERRAFORM_RESOURCE_MAPPING = ResourceMapping.builder()
        .terraformSourceName(TERRAFORM_SOURCE_NAME)
        .mappingType(ResourceMappingType.TERRAFORM)
        .physicalResourceId(PhysicalResourceId.builder()
            .type(PhysicalIdentifierType.NATIVE)
            .identifier("s3://my-bucket/state.tf")
            .build())
        .build();

    public static ResourceModel resourceModel(final App app) {
        return ResourceModel.builder()
            .appArn(app.appArn())
            .name(app.name())
            .description(app.description())
            .resiliencyPolicyArn(app.policyArn())
            .appAssessmentSchedule(app.assessmentScheduleAsString())
            .tags(app.tags())
            .build();
    }

    public static ResourceModel resourceModel(final AppSummary appSummary) {
        return ResourceModel.builder()
            .appArn(appSummary.appArn())
            .name(appSummary.name())
            .description(appSummary.description())
            .build();
    }

    public static ResourceModel resourceModel() {
        return resourceModel(createAppResponse(createAppRequest()).app());
    }

    public static CreateAppRequest createAppRequest() {
        return CreateAppRequest.builder()
            .name(APP_NAME)
            .description(APP_DESC)
            .policyArn(POLICY_ARN)
            .assessmentSchedule(AppAssessmentScheduleType.DAILY)
            .tags(ImmutableMap.of("t1", "v1"))
            .build();
    }

    public static CreateAppResponse createAppResponse(final CreateAppRequest createAppRequest) {
        return CreateAppResponse.builder()
            .app(app(createAppRequest))
            .build();
    }

    public static App app(final CreateAppRequest request) {
        return App.builder()
            .appArn(APP_ARN)
            .name(request.name())
            .description(request.description())
            .policyArn(POLICY_ARN)
            .status(AppStatusType.ACTIVE)
            .creationTime(Instant.now())
            .complianceStatus(AppComplianceStatusType.NOT_ASSESSED)
            .lastAppComplianceEvaluationTime(null)
            .resiliencyScore(null)
            .lastResiliencyScoreEvaluationTime(null)
            .assessmentSchedule(AppAssessmentScheduleType.DAILY)
            .tags(request.tags())
            .build();
    }

    public static App app() {
        return app(createAppRequest());
    }

    public static AppSummary appSummary() {
        return AppSummary.builder()
            .appArn(APP_ARN)
            .name(APP_NAME)
            .description(APP_DESC)
            .creationTime(Instant.now())
            .complianceStatus(AppComplianceStatusType.NOT_ASSESSED)
            .resiliencyScore(null)
            .build();
    }

    public static AppSummary appSummary(final App app) {
        return AppSummary.builder()
            .appArn(app.appArn())
            .name(app.name())
            .description(app.description())
            .creationTime(app.creationTime())
            .complianceStatus(app.complianceStatus())
            .resiliencyScore(app.resiliencyScore())
            .build();
    }

    public static DescribeAppResponse describeAppResponse(final App app) {
        return DescribeAppResponse.builder()
            .app(app)
            .build();
    }

    public static ResourceMapping generateResourceMapping(final String physicalResourceIdentifier) {
        return ResourceMapping.builder()
            .mappingType(ResourceMappingType.CFN_STACK)
            .physicalResourceId(PhysicalResourceId.builder()
                .type(PhysicalIdentifierType.ARN)
                .identifier(physicalResourceIdentifier)
                .build())
            .build();
    }
}
