package org.nurma.aqyndar.controller;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.response.InitiateTextToSpeechConversionResponse;
import org.nurma.aqyndar.dto.response.TextToSpeechResultResponse;
import org.nurma.aqyndar.service.TextToSpeechService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/voice")
public class VoiceController {
    private final TextToSpeechService textToSpeechService;

    @GetMapping("/convert")
    public InitiateTextToSpeechConversionResponse initiateTextToSpeechConversion(
            @RequestParam("text") final String text) {
        return textToSpeechService.processTextToSpeechAsync(text);
    }

    @GetMapping("/result/{uuid}")
    public TextToSpeechResultResponse getConversionResult(@PathVariable("uuid") final String requestId) {
        return textToSpeechService.getResult(requestId);
    }
}
