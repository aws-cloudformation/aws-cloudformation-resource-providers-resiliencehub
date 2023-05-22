package com.amazonaws.resiliencehub.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.resiliencehub.model.TagResourceRequest;
import software.amazon.awssdk.services.resiliencehub.model.UntagResourceRequest;
import software.amazon.cloudformation.proxy.ProxyClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class TaggingUtilTest {

    private static final String RESOURCE_ARN = "resourceArn";
    private static final String TAG_KEY_1 = "tagKey1";
    private static final String TAG_VALUE_1 = "tagValue1";
    private static final String TAG_KEY_2 = "tagKey2";
    private static final String TAG_VALUE_2 = "tagValue2";
    private static final String TAG_KEY_3 = "tagKey3";
    private static final String TAG_VALUE_3 = "tagValue3";

    @Mock
    private ResiliencehubClient resiliencehubClient;

    @Mock
    private ProxyClient<ResiliencehubClient> proxyClient;

    private TaggingUtil taggingUtil;

    @BeforeEach
    public void setup() {
        taggingUtil = new TaggingUtil();
        doReturn(resiliencehubClient).when(proxyClient).client();
    }

    @Test
    public void testListTagsForResource() {
        final ListTagsForResourceRequest listTagsForResourceRequest = ListTagsForResourceRequest.builder().build();
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder().build();

        doReturn(listTagsForResourceResponse).when(proxyClient).injectCredentialsAndInvokeV2(same(listTagsForResourceRequest), any());

        assertEquals(listTagsForResourceResponse, taggingUtil.listTagsForResource(listTagsForResourceRequest, proxyClient));
    }

    @Test
    public void testUpdateTags() {
        final Map<String, String> existingTags = ImmutableMap.of(
            TAG_KEY_1, TAG_VALUE_1,
            TAG_KEY_2, TAG_VALUE_2);
        final Map<String, String> desiredTags = ImmutableMap.of(
            TAG_KEY_2, TAG_VALUE_2,
            TAG_KEY_3, TAG_VALUE_3);
        final ListTagsForResourceRequest listTagsForResourceRequest = ListTagsForResourceRequest.builder()
            .resourceArn(RESOURCE_ARN)
            .build();
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder()
            .tags(existingTags)
            .build();
        doReturn(listTagsForResourceResponse).when(proxyClient).injectCredentialsAndInvokeV2(eq(listTagsForResourceRequest), any());

        taggingUtil.updateTags(RESOURCE_ARN, desiredTags, proxyClient);

        final TagResourceRequest expectedTagResourceRequest = TagResourceRequest.builder()
            .resourceArn(RESOURCE_ARN)
            .tags(ImmutableMap.of(TAG_KEY_3, TAG_VALUE_3))
            .build();
        final UntagResourceRequest expectedUntagResourceRequest = UntagResourceRequest.builder()
            .resourceArn(RESOURCE_ARN)
            .tagKeys(ImmutableSet.of(TAG_KEY_1))
            .build();
        verify(proxyClient).injectCredentialsAndInvokeV2(eq(expectedTagResourceRequest), any());
        verify(proxyClient).injectCredentialsAndInvokeV2(eq(expectedUntagResourceRequest), any());
    }

    @Test
    public void testUpdateTags_noTagsToAddOrRemove() {
        final Map<String, String> existingTags = ImmutableMap.of(TAG_KEY_1, TAG_VALUE_1);
        final Map<String, String> desiredTags = ImmutableMap.of(TAG_KEY_1, TAG_VALUE_1);
        final ListTagsForResourceRequest listTagsForResourceRequest = ListTagsForResourceRequest.builder()
            .resourceArn(RESOURCE_ARN)
            .build();
        final ListTagsForResourceResponse listTagsForResourceResponse = ListTagsForResourceResponse.builder()
            .tags(existingTags)
            .build();
        doReturn(listTagsForResourceResponse).when(proxyClient).injectCredentialsAndInvokeV2(eq(listTagsForResourceRequest), any());

        taggingUtil.updateTags(RESOURCE_ARN, desiredTags, proxyClient);

        verifyNoMoreInteractions(proxyClient);
    }

}
