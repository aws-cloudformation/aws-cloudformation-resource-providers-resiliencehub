package com.amazonaws.resiliencehub.common;

import software.amazon.awssdk.services.resiliencehub.ResiliencehubClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {

    private static ResiliencehubClient client;

    /**
     * Gets a ResilienceHub Client
     */
    public static ResiliencehubClient getClient() {
        if (client == null) {
            client = ResiliencehubClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .build();
        }
        return client;
    }
}
