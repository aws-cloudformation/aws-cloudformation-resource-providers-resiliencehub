package com.amazonaws.resiliencehub.app;

import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.resiliencehub.common.Constants;
import com.google.common.collect.Sets;

import software.amazon.awssdk.services.resiliencehub.model.AddDraftAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.App;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DeleteAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppVersionTemplateRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.resiliencehub.model.PhysicalIdentifierType;
import software.amazon.awssdk.services.resiliencehub.model.PublishAppVersionRequest;
import software.amazon.awssdk.services.resiliencehub.model.PutDraftAppVersionTemplateRequest;
import software.amazon.awssdk.services.resiliencehub.model.RemoveDraftAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ResourceMappingType;
import software.amazon.awssdk.services.resiliencehub.model.UpdateAppRequest;

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
    static CreateAppRequest translateToCreateAppRequest(final ResourceModel model) {
        return CreateAppRequest.builder()
            .name(model.getName())
            .description(model.getDescription())
            .policyArn(model.getResiliencyPolicyArn())
            .assessmentSchedule(model.getAppAssessmentSchedule())
            .tags(model.getTags())
            .build();
    }

    /**
     * Request to update a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to update a resource
     */
    static UpdateAppRequest translateToUpdateAppRequest(final ResourceModel model) {
        return UpdateAppRequest.builder()
            .appArn(model.getAppArn())
            .description(model.getDescription())
            .policyArn(model.getResiliencyPolicyArn())
            .assessmentSchedule(model.getAppAssessmentSchedule())
            .build();
    }

    /**
     * Request to read a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to describe a resource
     */
    static DescribeAppRequest translateToReadAppRequest(final ResourceModel model) {
        return DescribeAppRequest.builder()
            .appArn(model.getAppArn())
            .build();
    }

    /**
     * Translates resource object from sdk into a resource model
     *
     * @param describeAppResponse the aws service describe resource response
     * @return model resource model
     */
    static ResourceModel translateFromReadResponse(final DescribeAppResponse describeAppResponse) {
        // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L58-L73
        final App app = describeAppResponse.app();
        return ResourceModel.builder()
            .appArn(app.appArn())
            .name(app.name())
            .description(app.description())
            .resiliencyPolicyArn(app.policyArn())
            .appAssessmentSchedule(app.assessmentScheduleAsString())
            .tags(app.tags())
            .build();
    }

    /**
     * Request to delete a resource
     *
     * @param model resource model
     * @return awsRequest the aws service request to delete a resource
     */
    static DeleteAppRequest translateToDeleteAppRequest(final ResourceModel model) {
        return DeleteAppRequest.builder()
            .appArn(model.getAppArn())
            .forceDelete(false)
            .build();
    }

    /**
     * Request to list resources
     *
     * @param nextToken token passed to the aws service list resources request
     * @return awsRequest the aws service request to list resources within aws account
     */
    static ListAppsRequest translateToListAppRequest(final String nextToken) {
        return ListAppsRequest.builder()
            .nextToken(nextToken)
            .build();
    }

    /**
     * Request to list resources with appArn filter
     *
     * @param appArn AppArn filter
     * @return awsRequest the aws service request to list resources within aws account
     */
    static ListAppsRequest translateToListRequestWithAppArnFilter(final String appArn) {
        return ListAppsRequest.builder()
            .appArn(appArn)
            .build();
    }

    /**
     * Translates resource objects from sdk into a resource model (primary identifier only)
     *
     * @param listAppsResponse the aws service describe resource response
     * @return list of resource models
     */
    static List<ResourceModel> translateFromListResponse(final ListAppsResponse listAppsResponse) {
        // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L75-L82
        return streamOfOrEmpty(listAppsResponse.appSummaries())
            .map(appSummary -> ResourceModel.builder()
                .appArn(appSummary.appArn())
                .name(appSummary.name())
                .description(appSummary.description())
                .build())
            .collect(Collectors.toList());
    }

    /**
     * Request to add template to a draft App
     *
     * @param model resource model
     * @return awsRequest the aws service request to add template to an App
     */
    static PutDraftAppVersionTemplateRequest translateToPutDraftAppVersionTemplateRequest(final ResourceModel model) {
        return PutDraftAppVersionTemplateRequest.builder()
            .appArn(model.getAppArn())
            .appTemplateBody(model.getAppTemplateBody())
            .build();
    }

    /**
     * Request to add resource mappings to a draft App
     *
     * @param model resource model
     * @return awsRequest the aws service request to add resource mappings to an App
     */
    static AddDraftAppVersionResourceMappingsRequest translateToAddDraftAppVersionResourceMappingsRequest(
        final ResourceModel model) {
        return AddDraftAppVersionResourceMappingsRequest.builder()
            .appArn(model.getAppArn())
            .resourceMappings(Translator.toSdkResourceMappings(model.getResourceMappings()))
            .build();
    }

    /**
     * Request to add resource mappings to a draft App
     *
     * @param appArn application Arn
     * @param sdkResourceMappings application resourceMappings
     * @return awsRequest the aws service request to add resource mappings to an App
     */
    static AddDraftAppVersionResourceMappingsRequest translateToAddDraftAppVersionResourceMappingsRequest(final String appArn,
        final Set<software.amazon.awssdk.services.resiliencehub.model.ResourceMapping> sdkResourceMappings) {
        Validate.notBlank(appArn);
        Validate.notNull(sdkResourceMappings);

        return AddDraftAppVersionResourceMappingsRequest.builder()
            .appArn(appArn)
            .resourceMappings(sdkResourceMappings)
            .build();
    }

    /**
     * Request to remove resource mappings in a draft App
     *
     * @param appArn application Arn
     * @param sdkResourceMappings application resourceMappings
     * @return awsRequest the aws service request to remove resource mappings from an App
     */
    static RemoveDraftAppVersionResourceMappingsRequest translateToRemoveDraftAppVersionResourceMappingsRequest(final String appArn,
        final Set<software.amazon.awssdk.services.resiliencehub.model.ResourceMapping> sdkResourceMappings) {
        Validate.notBlank(appArn);
        Validate.notNull(sdkResourceMappings);

        final Set<String> logicalStackNames = Sets.newHashSet();
        final Set<String> resourceNames = Sets.newHashSet();
        final Set<String> terraformSourceNames = Sets.newHashSet();
        final Set<String> eksSourceNames = Sets.newHashSet();
        for (final software.amazon.awssdk.services.resiliencehub.model.ResourceMapping resourceMapping : sdkResourceMappings) {
            switch (resourceMapping.mappingType()) {
                case CFN_STACK:
                    logicalStackNames.add(resourceMapping.logicalStackName());
                    break;
                case RESOURCE:
                    resourceNames.add(resourceMapping.resourceName());
                    break;
                case TERRAFORM:
                    terraformSourceNames.add(resourceMapping.terraformSourceName());
                    break;
                case EKS:
                    eksSourceNames.add(resourceMapping.eksSourceName());
                    break;
            }
        }

        return RemoveDraftAppVersionResourceMappingsRequest.builder()
            .appArn(appArn)
            .logicalStackNames(logicalStackNames)
            .resourceNames(resourceNames)
            .terraformSourceNames(terraformSourceNames)
            .eksSourceNames(eksSourceNames)
            .build();
    }

    /**
     * Request to publish an App version
     *
     * @param model resource model
     * @return awsRequest the aws service request to publish an App version
     */
    static PublishAppVersionRequest translateToPublishAppVersionRequest(final ResourceModel model) {
        return PublishAppVersionRequest.builder()
            .appArn(model.getAppArn())
            .build();
    }

    /**
     * Request to describe App version template
     *
     * @param model resource model
     * @return awsRequest the aws service request to describe App version template.
     */
    static DescribeAppVersionTemplateRequest translateToDescribeAppVersionTemplateRequest(final ResourceModel model) {
        return DescribeAppVersionTemplateRequest.builder()
            .appArn(model.getAppArn())
            .appVersion(Constants.RELEASE_VERSION)
            .build();
    }

    /**
     * Request to list the resource mappings for an App
     *
     * @param appVersion App version to read mappings for
     * @param model resource model
     * @return awsRequest the aws service request to list resource mappings for an App
     */
    static ListAppVersionResourceMappingsRequest translateToListAppVersionResourceMappingsRequest(final String appVersion,
        final ResourceModel model) {
        Validate.notBlank(appVersion);
        Validate.notNull(model);
        return ListAppVersionResourceMappingsRequest.builder()
            .appArn(model.getAppArn())
            .appVersion(appVersion)
            .build();
    }

    /**
     * Request to convert the resource mappings for an App into CFN model
     *
     * @param sdkResourceMappings resource mappings
     * @return awsRequest the aws service request to convert resource mappings for an App into CFN model
     */
    static List<ResourceMapping> toCfnResourceMappings(
        final Set<software.amazon.awssdk.services.resiliencehub.model.ResourceMapping> sdkResourceMappings) {
        return sdkResourceMappings.stream()
            .map(Translator::toCfnResourceMapping)
            .collect(Collectors.toList());
    }

    /**
     * Request to list the tags for an App
     *
     * @param model resource model
     * @return awsRequest the aws service request to list tags for an App
     */
    static ListTagsForResourceRequest translateToListTagsForResourceRequest(final ResourceModel model) {
        return ListTagsForResourceRequest.builder()
            .resourceArn(model.getAppArn())
            .build();
    }

    /**
     * Takes a list of CFN resourceMapping objects and returns a set of SDK resourceMappings.
     *
     * @param cfnResourceMappings resource mappings
     * @return Set of SDK resource mappings
     */
    static Set<software.amazon.awssdk.services.resiliencehub.model.ResourceMapping> toSdkResourceMappings(
        final List<com.amazonaws.resiliencehub.app.ResourceMapping> cfnResourceMappings) {
        return cfnResourceMappings.stream()
            .map(Translator::toSdkResourceMapping)
            .collect(Collectors.toSet());
    }

    private static software.amazon.awssdk.services.resiliencehub.model.ResourceMapping toSdkResourceMapping(
        final com.amazonaws.resiliencehub.app.ResourceMapping cfnResourceMapping) {
        return software.amazon.awssdk.services.resiliencehub.model.ResourceMapping.builder()
            .logicalStackName(cfnResourceMapping.getLogicalStackName())
            .mappingType(ResourceMappingType.fromValue(cfnResourceMapping.getMappingType()))
            .physicalResourceId(toSdkPhysicalResourceId(cfnResourceMapping.getPhysicalResourceId()))
            .resourceName(cfnResourceMapping.getResourceName())
            .terraformSourceName(cfnResourceMapping.getTerraformSourceName())
            .eksSourceName(cfnResourceMapping.getEksSourceName())
            .build();
    }

    private static software.amazon.awssdk.services.resiliencehub.model.PhysicalResourceId toSdkPhysicalResourceId(
        final com.amazonaws.resiliencehub.app.PhysicalResourceId cfnPhysicalResourceId) {
        return software.amazon.awssdk.services.resiliencehub.model.PhysicalResourceId.builder()
            .awsAccountId(cfnPhysicalResourceId.getAwsAccountId())
            .awsRegion(cfnPhysicalResourceId.getAwsRegion())
            .identifier(cfnPhysicalResourceId.getIdentifier())
            .type(PhysicalIdentifierType.fromValue(cfnPhysicalResourceId.getType()))
            .build();
    }

    private static ResourceMapping toCfnResourceMapping(
        final software.amazon.awssdk.services.resiliencehub.model.ResourceMapping sdkResourceMapping) {
        return ResourceMapping.builder()
            .logicalStackName(sdkResourceMapping.logicalStackName())
            .terraformSourceName(sdkResourceMapping.terraformSourceName())
            .eksSourceName(sdkResourceMapping.eksSourceName())
            .mappingType(sdkResourceMapping.mappingType().toString())
            .physicalResourceId(toCfnPhysicalResourceId(sdkResourceMapping.physicalResourceId()))
            .resourceName(sdkResourceMapping.resourceName())
            .build();
    }

    private static PhysicalResourceId toCfnPhysicalResourceId(
        final software.amazon.awssdk.services.resiliencehub.model.PhysicalResourceId sdkPhysicalResourceId) {
        return PhysicalResourceId.builder()
            .awsAccountId(sdkPhysicalResourceId.awsAccountId())
            .awsRegion(sdkPhysicalResourceId.awsRegion())
            .identifier(sdkPhysicalResourceId.identifier())
            .type(sdkPhysicalResourceId.type().toString())
            .build();
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
    }

}
