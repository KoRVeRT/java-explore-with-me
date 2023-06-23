package ru.practicum.ewm.client.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.dto.stats.HitDto;
import ru.practicum.ewm.dto.stats.ViewDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsClient {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String STATS_URI = "/stats";
    private static final String HIT_URI = "/hit";

    private final WebClient client;
    private final DateTimeFormatter dateTimeFormat;

    @Autowired
    public StatsClient(WebClient.Builder webClientBuilder, String url) {
        this.client = webClientBuilder.baseUrl(url).build();
        dateTimeFormat = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    }

    public List<ViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path(STATS_URI)
                        .queryParam("start", start.format(dateTimeFormat))
                        .queryParam("end", end.format(dateTimeFormat))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(ViewDto.class)
                .collectList()
                .block();
    }

    public void postHit(HitDto hitDto) {
        client.post()
                .uri(HIT_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(hitDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}