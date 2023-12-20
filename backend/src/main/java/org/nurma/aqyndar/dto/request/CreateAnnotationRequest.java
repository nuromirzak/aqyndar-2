package org.nurma.aqyndar.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAnnotationRequest {
    @NotEmpty
    private String content;

    @NotEmpty
    private int startRangeIndex;

    @NotEmpty
    private int endRangeIndex;

    @NotEmpty
    private int poemId;
}
