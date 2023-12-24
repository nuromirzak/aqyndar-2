package org.nurma.aqyndar.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PerformanceBenchmark {
    private static final int ITERATIONS = 25;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(PerformanceBenchmark.class);

    public PerformanceBenchmark(final String baseUrl) {
        this.restTemplate = new RestTemplateBuilder()
                .rootUri(baseUrl)
                .build();
    }

    public static void main(final String[] args) {
        final String baseUrl = "http://localhost:8080";
        final PerformanceBenchmark benchmark = new PerformanceBenchmark(baseUrl);

        double totalTopUsersTime = 0;
        double totalTopPoemsTime = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            totalTopUsersTime += benchmark.topUsers();
            totalTopPoemsTime += benchmark.topPoems();
        }

        benchmark.logger.info("Average top users time: {} ms", totalTopUsersTime / ITERATIONS);
        benchmark.logger.info("Average top poems time: {} ms", totalTopPoemsTime / ITERATIONS);
    }

    private long topUsers() {
        long endTime = System.currentTimeMillis();

        restTemplate.getForObject("/reaction/top?topEntity=USER", List.class);

        long startTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    private long topPoems() {
        long endTime = System.currentTimeMillis();

        restTemplate.getForObject("/reaction/top?topEntity=POEM", List.class);

        long startTime = System.currentTimeMillis();

        return endTime - startTime;
    }
}
