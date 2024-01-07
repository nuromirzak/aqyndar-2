package org.nurma.aqyndar.service;

import lombok.extern.log4j.Log4j2;
import org.nurma.aqyndar.dto.response.InitiateTextToSpeechConversionResponse;
import org.nurma.aqyndar.dto.response.TextToSpeechResultResponse;
import org.nurma.aqyndar.entity.enums.TextToSpeechStatus;
import org.nurma.aqyndar.exception.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class TextToSpeechService {
    public static final int MAX_TEXT_LENGTH = 500;
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final AudioStorageService audioStorageService;
    private final ConcurrentHashMap<String, TextToSpeechResultResponse> results;
    private final AsyncTaskExecutor textToSpeechExecutor;

    public TextToSpeechService(@Value("${voice-service.base-url}") final String baseUrl,
                               final AudioStorageService audioStorageService,
                               @Qualifier("textToSpeechExecutor") final AsyncTaskExecutor textToSpeechExecutor,
                               final RestTemplate textToSpeechRestTemplate) {
        this.baseUrl = baseUrl;
        this.audioStorageService = audioStorageService;
        this.results = new ConcurrentHashMap<>();
        this.textToSpeechExecutor = textToSpeechExecutor;
        this.restTemplate = textToSpeechRestTemplate;
    }

    public InitiateTextToSpeechConversionResponse processTextToSpeechAsync(final String text) {
        if (text != null && text.length() > MAX_TEXT_LENGTH) {
            throw new ValidationException("Text length should be less than " + MAX_TEXT_LENGTH);
        }

        String requestId = UUID.randomUUID().toString();
        results.put(requestId, new TextToSpeechResultResponse(TextToSpeechStatus.PROCESSING));

        textToSpeechExecutor.execute(() -> {
            processTextToSpeech(requestId, text);
        });

        return new InitiateTextToSpeechConversionResponse(requestId);
    }

    private void processTextToSpeech(final String requestId, final String text) {
        try {
            String url = baseUrl + "/api/text2speech?text=" + text;
            byte[] audioContent = restTemplate.getForObject(url, byte[].class);
            if (audioContent != null) {
                String fileName = audioStorageService.store(audioContent);
                TextToSpeechResultResponse result =
                        new TextToSpeechResultResponse(TextToSpeechStatus.COMPLETED, fileName);
                results.put(requestId, result);
            } else {
                TextToSpeechResultResponse result =
                        new TextToSpeechResultResponse(TextToSpeechStatus.FAILED, "Audio content is null");
                results.put(requestId, result);
            }
        } catch (Exception e) {
            log.error("Error while processing text to speech with request ID: " + requestId, e);
            TextToSpeechResultResponse result =
                    new TextToSpeechResultResponse(TextToSpeechStatus.FAILED, e.getMessage());
            results.put(requestId, result);
        }
    }

    public TextToSpeechResultResponse getResult(final String requestId) {
        TextToSpeechResultResponse result = results.get(requestId);
        if (result == null) {
            return new TextToSpeechResultResponse(TextToSpeechStatus.FAILED, "Request ID not found");
        }
        return result;
    }
}
