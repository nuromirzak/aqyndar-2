package org.nurma.aqyndar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nurma.aqyndar.entity.enums.TextToSpeechStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextToSpeechResultResponse {
    private TextToSpeechStatus status;
    private String url;
    private String error;

    public TextToSpeechResultResponse(final TextToSpeechStatus status) {
        this.status = status;
    }

    public TextToSpeechResultResponse(final TextToSpeechStatus status, final String str) {
        this.status = status;
        switch (status) {
            case COMPLETED:
                this.url = str;
                break;
            case FAILED:
                this.error = str;
                break;
            case PROCESSING:
                break;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
