package com.amazonaws.resiliencehub.app;

import java.time.Instant;

import com.google.common.collect.ImmutableMap;

import software.amazon.awssdk.services.resiliencehub.model.App;
import software.amazon.awssdk.services.resiliencehub.model.AppAssessmentScheduleType;
import software.amazon.awssdk.services.resiliencehub.model.AppComplianceStatusType;
import software.amazon.awssdk.services.resiliencehub.model.AppDriftStatusType;
import software.amazon.awssdk.services.resiliencehub.model.AppStatusType;
import software.amazon.awssdk.services.resiliencehub.model.AppSummary;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.EventSubscription;
import software.amazon.awssdk.services.resiliencehub.model.EventType;
import software.amazon.awssdk.services.resiliencehub.model.PermissionModel;
import software.amazon.awssdk.services.resiliencehub.model.PermissionModelType;
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
    public static final String EKS_SOURCE_NAME = "eksSourceName";
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
    public static final ResourceMapping EKS_RESOURCE_MAPPING = ResourceMapping.builder()
        .eksSourceName(EKS_SOURCE_NAME)
        .mappingType(ResourceMappingType.EKS)
        .physicalResourceId(PhysicalResourceId.builder()
            .type(PhysicalIdentifierType.ARN)
            .identifier("arn:aws:eks:us-west-2:0123456789:cluster/eksCluster")
            .build())
        .build();

    public static ResourceModel resourceModel(final App app) {
        return ResourceModel.builder()
            .appArn(app.appArn())
            .name(app.name())
            .description(app.description())
            .resiliencyPolicyArn(app.policyArn())
            .appAssessmentSchedule(app.assessmentScheduleAsString())
            .permissionModel(Translator.toCfnPermissionModel(app.permissionModel()))
            .eventSubscriptions(Translator.toCfnEventSubscriptions(app.eventSubscriptions()))
            .driftStatus(app().driftStatusAsString())
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
            .permissionModel(PermissionModel.builder()
                .invokerRoleName("invoker-role-name")
                .crossAccountRoleArns("arn:aws:iam::012345678912:role/cross-account-role-name")
                .type(PermissionModelType.ROLE_BASED)
                .build())
            .eventSubscriptions(EventSubscription.builder()
                .name("even-subscription-name")
                .snsTopicArn("arn:aws:sns:us-west-2:012345678912:sns-topic-name")
                .eventType(EventType.DRIFT_DETECTED)
                .build())
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
            .permissionModel(request.permissionModel())
            .eventSubscriptions(request.eventSubscriptions())
            .driftStatus(AppDriftStatusType.NOT_CHECKED)
            .lastDriftEvaluationTime(null)
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
