package org.nurma.aqyndar.controller;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.nurma.aqyndar.configuration.TestDataFactory;
import org.nurma.aqyndar.configuration.TestRestTemplateConfig;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.response.InitiateTextToSpeechConversionResponse;
import org.nurma.aqyndar.service.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = {
        TestRestTemplateConfig.class
})
public class VoiceControllerTest extends TestDataFactory {
    private static final String uuidRegex =
            "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$";

    @Autowired
    private RestTemplate restTemplateMock;

    private final Faker faker = new Faker();

    @Test
    void testInitiateTextToSpeechConversion() throws Exception {
        String text = faker.lorem().paragraph();

        initiateTextToSpeechConversion(text)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", matchesPattern(uuidRegex)));
    }

    @Test
    void testExceedingMaxTextLengthToSpeechConversion() throws Exception {
        String text = faker.lorem().fixedString(TextToSpeechService.MAX_TEXT_LENGTH + 1);

        initiateTextToSpeechConversion(text)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    void testGetConversionResult() throws Exception {
        String text = faker.lorem().paragraph();

        Mockito.when(restTemplateMock.getForObject(Mockito.anyString(), Mockito.any()))
                .thenReturn(new byte[] {1, 2, 3});

        InitiateTextToSpeechConversionResponse initiateTextToSpeechConversionResponse = fromJson(
                initiateTextToSpeechConversion(text),
                InitiateTextToSpeechConversionResponse.class
        );

        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            getConversionResult(initiateTextToSpeechConversionResponse.getUuid())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("COMPLETED"))
                    .andExpect(jsonPath("$.url", matchesPattern(".*\\.wav")));
        });
    }

    @Test
    void testWhenAudioContentIsNull() throws Exception {
        String text = faker.lorem().paragraph();

        Mockito.when(restTemplateMock.getForObject(Mockito.anyString(), Mockito.any()))
                .thenReturn(null);

        InitiateTextToSpeechConversionResponse initiateTextToSpeechConversionResponse = fromJson(
                initiateTextToSpeechConversion(text),
                InitiateTextToSpeechConversionResponse.class
        );

        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            getConversionResult(initiateTextToSpeechConversionResponse.getUuid())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.url").doesNotExist());
        });
    }

    @Test
    void testFailedConversionResult() throws Exception {
        String text = faker.lorem().paragraph();

        Mockito.when(restTemplateMock.getForObject(Mockito.anyString(), Mockito.any()))
                .thenAnswer((Answer<byte[]>) invocation -> {
                    throw new Exception("Exception from mockito!!!");
                });

        InitiateTextToSpeechConversionResponse initiateTextToSpeechConversionResponse = fromJson(
                initiateTextToSpeechConversion(text),
                InitiateTextToSpeechConversionResponse.class
        );

        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            getConversionResult(initiateTextToSpeechConversionResponse.getUuid())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("FAILED"))
                    .andExpect(jsonPath("$.url").doesNotExist());
        });
    }

    @Test
    void testNotYetReadyConversionResult() throws Exception {
        String text = faker.lorem().paragraph();

        Mockito.when(restTemplateMock.getForObject(Mockito.anyString(), Mockito.any()))
                .thenAnswer((Answer<byte[]>) invocation -> {
                    Thread.sleep(5000);
                    return new byte[] {1, 2, 3};
                });

        InitiateTextToSpeechConversionResponse initiateTextToSpeechConversionResponse = fromJson(
                initiateTextToSpeechConversion(text),
                InitiateTextToSpeechConversionResponse.class
        );

        Awaitility.await().during(2, TimeUnit.SECONDS).untilAsserted(() -> {
            getConversionResult(initiateTextToSpeechConversionResponse.getUuid())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("PROCESSING"))
                    .andExpect(jsonPath("$.url").doesNotExist());
        });
    }

    @Test
    void testYetReadyConversionResult() throws Exception {
        String text = faker.lorem().paragraph();

        Mockito.when(restTemplateMock.getForObject(Mockito.anyString(), Mockito.any()))
                .thenAnswer((Answer<byte[]>) invocation -> {
                    Thread.sleep(500);
                    return new byte[] {1, 2, 3};
                });

        InitiateTextToSpeechConversionResponse initiateTextToSpeechConversionResponse = fromJson(
                initiateTextToSpeechConversion(text),
                InitiateTextToSpeechConversionResponse.class
        );

        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            getConversionResult(initiateTextToSpeechConversionResponse.getUuid())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("COMPLETED"))
                    .andExpect(jsonPath("$.url", matchesPattern(".*\\.wav")));
        });
    }

    @Test
    void testRetrievingNotExistingConversionResult() throws Exception {
        getConversionResult("not-existing-uuid")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.url").doesNotExist());
    }
}
