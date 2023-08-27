package com.amazonaws.resiliencehub.common;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.resiliencehub.model.TagResourceRequest;
import software.amazon.awssdk.services.resiliencehub.model.UntagResourceRequest;
import software.amazon.cloudformation.proxy.ProxyClient;

public class TaggingUtil {

    private static final String TAG_RESOURCE = "TagResource";
    private static final String UNTAG_RESOURCE = "UntagResource";
    private static final String LIST_TAGS_FOR_RESOURCE = "ListTagsForResource";

    // Our ListTagsForResource doesn't support nextToken for pagination
    public static ListTagsForResourceResponse listTagsForResource(
        final ListTagsForResourceRequest request,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notNull(request);
        Validate.notNull(proxyClient);

        return ExceptionHandlerWrapper.wrapResilienceHubExceptions(LIST_TAGS_FOR_RESOURCE,
            () -> proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::listTagsForResource));
    }

    public static void updateTags(
        final String resourceArn,
        final Map<String, String> tags,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notBlank(resourceArn);
        Validate.notNull(proxyClient);

        final ListTagsForResourceRequest listTagsForResourceRequest = ListTagsForResourceRequest.builder()
            .resourceArn(resourceArn)
            .build();
        final Map<String, String> desiredTags = tags != null ? tags : new HashMap<>();
        final Map<String, String> existingTags = listTagsForResource(listTagsForResourceRequest, proxyClient).tags();
        final Map<String, String> tagsToRemove = Maps.difference(existingTags, desiredTags).entriesOnlyOnLeft();
        final Map<String, String> tagsToAdd = Maps.difference(desiredTags, existingTags).entriesOnlyOnLeft();

        removeTags(resourceArn, tagsToRemove.keySet(), proxyClient);
        addTags(resourceArn, tagsToAdd, proxyClient);
    }

    private static void addTags(
        final String resourceArn,
        final Map<String, String> tagMap,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notBlank(resourceArn);
        Validate.notNull(tagMap);
        Validate.notNull(proxyClient);

        if (!tagMap.isEmpty()) {
            final TagResourceRequest request = TagResourceRequest.builder()
                .resourceArn(resourceArn)
                .tags(tagMap)
                .build();

            ExceptionHandlerWrapper.wrapResilienceHubExceptions(TAG_RESOURCE,
                () -> proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::tagResource));
        }
    }

    private static void removeTags(
        final String resourceArn,
        final Set<String> tagKeysToRemove,
        final ProxyClient<ResiliencehubClient> proxyClient) {
        Validate.notBlank(resourceArn);
        Validate.notNull(proxyClient);

        if (CollectionUtils.isNotEmpty(tagKeysToRemove)) {
            final UntagResourceRequest request = UntagResourceRequest.builder()
                .resourceArn(resourceArn)
                .tagKeys(tagKeysToRemove)
                .build();

            ExceptionHandlerWrapper.wrapResilienceHubExceptions(UNTAG_RESOURCE,
                () -> proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::untagResource));
        }
    }

}
