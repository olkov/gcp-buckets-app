package com.test.gcp.bucket.config;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class StorageConfig {

    @Bean
    @Profile("default")
    public Storage gcpStorage() throws IOException {
        return StorageOptions.getDefaultInstance().getService();
    }

    @Bean
    @Profile("local")
    public Storage gcpStorageLocal(
        @Value("${gcp.projectId}") String projectId,
        @Value("${gcp.credentials.location}") String credentialsLocation
    ) throws IOException {

        Credentials credentials =
            GoogleCredentials.fromStream(new FileInputStream(credentialsLocation));

        StorageOptions storageOptions =
            StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build();

        return storageOptions.getService();
    }
}
