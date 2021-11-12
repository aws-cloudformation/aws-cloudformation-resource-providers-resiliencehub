package com.amazonaws.resiliencehub.app;

import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.awssdk.services.resiliencehub.model.AddDraftAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.AddDraftAppVersionResourceMappingsResponse;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.CreateAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DeleteAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DeleteAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.DescribeAppResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppVersionResourceMappingsResponse;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ListAppsResponse;
import software.amazon.awssdk.services.resiliencehub.model.PublishAppVersionRequest;
import software.amazon.awssdk.services.resiliencehub.model.PublishAppVersionResponse;
import software.amazon.awssdk.services.resiliencehub.model.PutDraftAppVersionTemplateRequest;
import software.amazon.awssdk.services.resiliencehub.model.PutDraftAppVersionTemplateResponse;
import software.amazon.awssdk.services.resiliencehub.model.RemoveDraftAppVersionResourceMappingsRequest;
import software.amazon.awssdk.services.resiliencehub.model.ResourceMapping;
import software.amazon.awssdk.services.resiliencehub.model.UpdateAppRequest;
import software.amazon.awssdk.services.resiliencehub.model.UpdateAppResponse;
import software.amazon.cloudformation.proxy.ProxyClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiCallsWrapperTest {

    private static final String PHYSICAL_RESOURCE_IDENTIFIER_1 = "Identifier-1";
    private static final String PHYSICAL_RESOURCE_IDENTIFIER_2 = "Identifier-2";
    private static final String NEXT_TOKEN = "nextToken";

    @Mock
    private ResiliencehubClient resiliencehubClient;

    @Mock
    private ProxyClient<ResiliencehubClient> proxyClient;

    private ApiCallsWrapper apiCallsWrapper;

    @BeforeEach
    public void setup() {
        apiCallsWrapper = new ApiCallsWrapper();
    }

    @Test
    public void testCreateApp() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final CreateAppRequest createAppRequest = CreateAppRequest.builder().build();
        final CreateAppResponse createAppResponse = CreateAppResponse.builder().build();

        doReturn(createAppResponse).when(proxyClient).injectCredentialsAndInvokeV2(same(createAppRequest), any());

        assertEquals(createAppResponse, apiCallsWrapper.createApp(createAppRequest, proxyClient));
    }

    @Test
    public void testDescribeApp() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final DescribeAppRequest describeAppRequest = DescribeAppRequest.builder().build();
        final DescribeAppResponse describeAppResponse = DescribeAppResponse.builder().build();

        doReturn(describeAppResponse).when(proxyClient).injectCredentialsAndInvokeV2(same(describeAppRequest), any());

        assertEquals(describeAppResponse, apiCallsWrapper.describeApp(describeAppRequest, proxyClient));
    }

    @Test
    public void testUpdateApp() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final UpdateAppRequest updateAppRequest = UpdateAppRequest.builder().build();
        final UpdateAppResponse updateAppResponse = UpdateAppResponse.builder().build();

        doReturn(updateAppResponse).when(proxyClient).injectCredentialsAndInvokeV2(same(updateAppRequest), any());

        assertEquals(updateAppResponse, apiCallsWrapper.updateApp(updateAppRequest, proxyClient));
    }

    @Test
    public void testDeleteApp() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final DeleteAppRequest deleteAppRequest = DeleteAppRequest.builder().build();
        final DeleteAppResponse deleteAppResponse = DeleteAppResponse.builder().build();

        doReturn(deleteAppResponse).when(proxyClient).injectCredentialsAndInvokeV2(same(deleteAppRequest), any());

        assertEquals(deleteAppResponse, apiCallsWrapper.deleteApp(deleteAppRequest, proxyClient));
    }

    @Test
    public void testListApps() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final ListAppsRequest listAppsRequest = ListAppsRequest.builder().build();
        final ListAppsResponse listAppsResponse = ListAppsResponse.builder().build();

        doReturn(listAppsResponse).when(proxyClient).injectCredentialsAndInvokeV2(same(listAppsRequest), any());

        assertEquals(listAppsResponse, apiCallsWrapper.listApps(listAppsRequest, proxyClient));
    }

    @Test
    public void testPutDraftAppVersionTemplate() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final PutDraftAppVersionTemplateRequest putDraftAppVersionTemplateRequest = PutDraftAppVersionTemplateRequest
            .builder().build();
        final PutDraftAppVersionTemplateResponse putDraftAppVersionTemplateResponse = PutDraftAppVersionTemplateResponse
            .builder().build();

        doReturn(putDraftAppVersionTemplateResponse).when(proxyClient)
            .injectCredentialsAndInvokeV2(same(putDraftAppVersionTemplateRequest), any());

        assertEquals(putDraftAppVersionTemplateResponse,
            apiCallsWrapper.putDraftAppVersionTemplate(putDraftAppVersionTemplateRequest, proxyClient));
    }

    @Test
    public void testAddDraftAppVersionResourceMappings() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final AddDraftAppVersionResourceMappingsRequest addDraftAppVersionResourceMappingsRequest = AddDraftAppVersionResourceMappingsRequest
            .builder().build();
        final AddDraftAppVersionResourceMappingsResponse addDraftAppVersionResourceMappingsResponse = AddDraftAppVersionResourceMappingsResponse
            .builder().build();

        doReturn(addDraftAppVersionResourceMappingsResponse).when(proxyClient)
            .injectCredentialsAndInvokeV2(same(addDraftAppVersionResourceMappingsRequest), any());

        assertEquals(addDraftAppVersionResourceMappingsResponse, apiCallsWrapper
            .addDraftAppVersionResourceMappings(addDraftAppVersionResourceMappingsRequest, proxyClient));
    }

    @Test
    public void testAddDraftAppVersionResourceMappings_withResourceMappingsAsInput() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final Set<software.amazon.awssdk.services.resiliencehub.model.ResourceMapping> resourceMappings = ImmutableSet
            .of(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING);
        final AddDraftAppVersionResourceMappingsRequest expectedRequest = AddDraftAppVersionResourceMappingsRequest
            .builder()
            .appArn(TestDataProvider.APP_ARN)
            .resourceMappings(resourceMappings)
            .build();

        apiCallsWrapper.addDraftAppVersionResourceMappings(TestDataProvider.APP_ARN, resourceMappings, proxyClient);

        verify(proxyClient).injectCredentialsAndInvokeV2(eq(expectedRequest), any());
    }

    @Test
    public void testAddDraftAppVersionResourceMappings_withEmptyResourceMappings() {
        final Set<software.amazon.awssdk.services.resiliencehub.model.ResourceMapping> resourceMappings = ImmutableSet.of();

        apiCallsWrapper.addDraftAppVersionResourceMappings(TestDataProvider.APP_ARN, resourceMappings, proxyClient);

        verify(proxyClient, never()).injectCredentialsAndInvokeV2(any(), any());
    }

    @Test
    public void testRemoveDraftAppVersionResourceMappings() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final Set<software.amazon.awssdk.services.resiliencehub.model.ResourceMapping> resourceMappings = ImmutableSet
            .of(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING, TestDataProvider.NATIVE_SDK_RESOURCE_MAPPING);
        final RemoveDraftAppVersionResourceMappingsRequest expectedRequest = RemoveDraftAppVersionResourceMappingsRequest.builder()
            .appArn(TestDataProvider.APP_ARN)
            .logicalStackNames(TestDataProvider.CFN_BACKED_SDK_RESOURCE_MAPPING.logicalStackName())
            .resourceNames(TestDataProvider.RESOURCE_NAME)
            .build();

        apiCallsWrapper.removeDraftAppVersionResourceMappings(TestDataProvider.APP_ARN, resourceMappings, proxyClient);

        verify(proxyClient).injectCredentialsAndInvokeV2(eq(expectedRequest), any());
    }

    @Test
    public void testRemoveDraftAppVersionResourceMappings_emptyResourceMappings() {
        final Set<software.amazon.awssdk.services.resiliencehub.model.ResourceMapping> resourceMappings = ImmutableSet.of();

        apiCallsWrapper.removeDraftAppVersionResourceMappings(TestDataProvider.APP_ARN, resourceMappings, proxyClient);

        verify(proxyClient, never()).injectCredentialsAndInvokeV2(any(), any());
    }

    @Test
    public void testPublishAppVersion() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final PublishAppVersionRequest publishAppVersionRequest = PublishAppVersionRequest.builder().build();
        final PublishAppVersionResponse publishAppVersionResponse = PublishAppVersionResponse.builder().build();

        doReturn(publishAppVersionResponse).when(proxyClient)
            .injectCredentialsAndInvokeV2(same(publishAppVersionRequest), any());

        assertEquals(publishAppVersionResponse,
            apiCallsWrapper.publishAppVersion(publishAppVersionRequest, proxyClient));
    }

    @Test
    public void testDescribeAppVersionTemplate() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final PutDraftAppVersionTemplateRequest putDraftAppVersionTemplateRequest = PutDraftAppVersionTemplateRequest
            .builder().build();
        final PutDraftAppVersionTemplateResponse putDraftAppVersionTemplateResponse = PutDraftAppVersionTemplateResponse
            .builder().build();

        doReturn(putDraftAppVersionTemplateResponse).when(proxyClient)
            .injectCredentialsAndInvokeV2(same(putDraftAppVersionTemplateRequest), any());

        assertEquals(putDraftAppVersionTemplateResponse,
            apiCallsWrapper.putDraftAppVersionTemplate(putDraftAppVersionTemplateRequest, proxyClient));
    }

    @Test
    public void testFetchAllResourceMappings() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final ListAppVersionResourceMappingsRequest resourceMappingsRequest = ListAppVersionResourceMappingsRequest
            .builder().build();
        final ListAppVersionResourceMappingsResponse resourceMappingsResponse = ListAppVersionResourceMappingsResponse
            .builder().build();

        doReturn(resourceMappingsResponse).when(proxyClient)
            .injectCredentialsAndInvokeV2(any(ListAppVersionResourceMappingsRequest.class), any());

        final Set<software.amazon.awssdk.services.resiliencehub.model.ResourceMapping> actualResourceMappings = apiCallsWrapper
            .fetchAllResourceMappings(resourceMappingsRequest, proxyClient);

        assertTrue(CollectionUtils.isEqualCollection(resourceMappingsResponse.resourceMappings(), actualResourceMappings));
        verify(proxyClient).injectCredentialsAndInvokeV2(eq(resourceMappingsRequest), any());
    }

    @Test
    public void testFetchAllResourceMappings_MultiplePages() {
        when(proxyClient.client()).thenReturn(resiliencehubClient);
        final software.amazon.awssdk.services.resiliencehub.model.ResourceMapping firstMapping = TestDataProvider
            .generateResourceMapping(PHYSICAL_RESOURCE_IDENTIFIER_1);
        final software.amazon.awssdk.services.resiliencehub.model.ResourceMapping secondMapping = TestDataProvider
            .generateResourceMapping(PHYSICAL_RESOURCE_IDENTIFIER_2);

        // 1st page
        final ListAppVersionResourceMappingsRequest firstPageRequest = ListAppVersionResourceMappingsRequest.builder()
            .nextToken(null)
            .build();
        final ListAppVersionResourceMappingsResponse firstPageResponse = ListAppVersionResourceMappingsResponse
            .builder()
            .resourceMappings(ImmutableList.of(firstMapping))
            .nextToken(NEXT_TOKEN)
            .build();

        // 2nd page
        final ListAppVersionResourceMappingsRequest secondPageRequest = ListAppVersionResourceMappingsRequest.builder()
            .nextToken(NEXT_TOKEN)
            .build();
        final ListAppVersionResourceMappingsResponse secondPageResponse = ListAppVersionResourceMappingsResponse
            .builder()
            .resourceMappings(ImmutableList.of(secondMapping))
            .build();

        when(proxyClient.injectCredentialsAndInvokeV2(any(ListAppVersionResourceMappingsRequest.class), any()))
            .thenReturn(firstPageResponse)
            .thenReturn(secondPageResponse);

        final Set<ResourceMapping> actualResourceMappings = apiCallsWrapper
            .fetchAllResourceMappings(firstPageRequest, proxyClient);

        assertTrue(CollectionUtils.isEqualCollection(ImmutableSet.of(firstMapping, secondMapping), actualResourceMappings));
        final ArgumentCaptor<ListAppVersionResourceMappingsRequest> resourceMappingsRequestArgumentCaptor = ArgumentCaptor
            .forClass(ListAppVersionResourceMappingsRequest.class);
        verify(proxyClient, times(2))
            .injectCredentialsAndInvokeV2(resourceMappingsRequestArgumentCaptor.capture(), any());
        assertTrue(CollectionUtils.isEqualCollection(ImmutableList.of(firstPageRequest, secondPageRequest),
            resourceMappingsRequestArgumentCaptor.getAllValues()));
    }
}
