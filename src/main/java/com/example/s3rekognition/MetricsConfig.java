package com.example.s3rekognition;

import com.amazonaws.regions.Regions;
import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

import java.time.Duration;
import java.util.Map;

@Configuration
public class MetricsConfig {

    @Bean
    CloudWatchConfig cloudWatchConfig(@Value("${cloudwatch.namespace}") String applicationId) {
        return new CloudWatchConfig() {
            private final Map<String, String> configuration = Map.of(
                    "cloudwatch.namespace", applicationId,
                    "cloudwatch.step", Duration.ofSeconds(5).toString());

            @Override
            public String get(String key) {
                return configuration.get(key);
            }
        };
    }


    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient(Regions regions) {
        return CloudWatchAsyncClient
                .builder()
                .region(Region.of(regions.getName()))
                .build();
    }

    @Bean
    public MeterRegistry getMeterRegistry(CloudWatchConfig cloudWatchConfig, CloudWatchAsyncClient cloudWatchAsyncClient) {
        return
                new CloudWatchMeterRegistry(
                        cloudWatchConfig,
                        Clock.SYSTEM,
                        cloudWatchAsyncClient);
    }
}
