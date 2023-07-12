package org.blossom.configuration;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.support.HttpHeaders;

@Configuration
public class ElasticConfiguration extends ElasticsearchConfiguration {
    @Value("${spring.data.elasticsearch.headers.authorization}")
    private String apiKey;

    @Value("${spring.data.elasticsearch.ssl.ca-fingerprint}")
    private String sslFingerprint;

    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String host;

    @Value("${spring.data.elasticsearch.local-dev-enabled}")
    private boolean localDevEnabled;

    @Value("${spring.data.elasticsearch.ssl.ssl-enabled}")
    private boolean sslEnabled;

    @Override
    @Nonnull
    public ClientConfiguration clientConfiguration() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", apiKey);

        ClientConfiguration.builder();

        ClientConfiguration.ClientConfigurationBuilderWithRequiredEndpoint elasticClientConfiguration = ClientConfiguration.builder();

        ClientConfiguration.MaybeSecureClientConfigurationBuilder maybeSecureClientConfigurationBuilder = localDevEnabled ?
                elasticClientConfiguration.connectedToLocalhost() : elasticClientConfiguration.connectedTo(host);

        if (sslEnabled) {
            maybeSecureClientConfigurationBuilder.usingSsl(sslFingerprint);
        }

        return maybeSecureClientConfigurationBuilder.withDefaultHeaders(headers).build();
    }
}