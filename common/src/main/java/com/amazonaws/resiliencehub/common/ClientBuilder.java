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
//                Uncomment this line to force CFN to use your endpoint ↓
//                .endpointOverride(URI.create("https://YOUR_ALIAS.people.aws.dev/"))
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .build();
        }
        return client;
    }
}
