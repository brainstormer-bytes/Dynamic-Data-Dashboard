package com.axlero.dashboard.Criteria_API.seed;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "app.seed")
public record SeedProperties(
        String demoPassword,
        String workspaceName,
        String datasetName,
        List<SeedUser> users) {

    public record SeedUser(String username, String email, String role) {}
}
