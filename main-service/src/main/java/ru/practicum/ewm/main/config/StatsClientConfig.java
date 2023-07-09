package ru.practicum.ewm.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.client.stats.StatsClient;

@Configuration
public class StatsClientConfig {
    @Value("${stats-server.url}")
    private String serverUrl;

    @Bean
    public StatsClient createEndpointHitClient() {
        return new StatsClient(serverUrl);
    }
}