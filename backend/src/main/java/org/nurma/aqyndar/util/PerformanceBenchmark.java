package org.nurma.aqyndar.util;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PerformanceBenchmark {
    private static final int ITERATIONS = 25;
    private static final String TOP_USERS_ENDPOINT = "/reaction/top?topEntity=USER";
    private static final String TOP_POEMS_ENDPOINT = "/reaction/top?topEntity=POEM";
    private static final String TEXT2SPEECH_ENDPOINT = "/api/text2speech?text={text}";
    private static final int MAX_SERVER_CAPACITY = 500;
    private static final int SYMBOLS_STEP = 10;
    private static final long STOP_THRESHOLD_TIME = TimeUnit.MINUTES.toMillis(3);

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(PerformanceBenchmark.class);
    private final Faker faker = new Faker();

    public PerformanceBenchmark(final String baseUrl) {
        this.restTemplate = new RestTemplateBuilder().rootUri(baseUrl).build();
    }

    public static void main(final String[] args) {
//        new PerformanceBenchmark("http://localhost:8080").runTopUsersAndPoemsBenchmark();
        new PerformanceBenchmark("http://localhost:8000").runVoiceTextBenchmark();
    }

    private void runTopUsersAndPoemsBenchmark() {
        double totalTopUsersTime = 0;
        double totalTopPoemsTime = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            totalTopUsersTime += measureExecutionTime(() -> restTemplate.getForObject(TOP_USERS_ENDPOINT, List.class));
            totalTopPoemsTime += measureExecutionTime(() -> restTemplate.getForObject(TOP_POEMS_ENDPOINT, List.class));
        }

        logger.info("Average top users time: {} ms", totalTopUsersTime / ITERATIONS);
        logger.info("Average top poems time: {} ms", totalTopPoemsTime / ITERATIONS);
    }

    private void runVoiceTextBenchmark() {
        for (int i = 1; i <= MAX_SERVER_CAPACITY; i += SYMBOLS_STEP) {
            int finalI = i;
            long voiceTextTime = measureExecutionTime(() -> voiceText(finalI));
            logger.info("VoiceTextTime for {} symbols: {} ms", i, voiceTextTime);
            if (voiceTextTime >= STOP_THRESHOLD_TIME) {
                break;
            }
        }
    }

    private long measureExecutionTime(final Runnable task) {
        long startTime = System.currentTimeMillis();
        task.run();
        return System.currentTimeMillis() - startTime;
    }

    private void voiceText(final int nSymbols) {
        byte[] audio =
                restTemplate.getForObject(TEXT2SPEECH_ENDPOINT, byte[].class, faker.lorem().characters(nSymbols));
        if (audio == null) {
            logger.warn("Audio is null for {} symbols", nSymbols);
        }
    }
}
