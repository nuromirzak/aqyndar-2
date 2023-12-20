package org.nurma.aqyndar.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchAnnotationRequest {
    @NotEmpty
    private String content;

    @NotEmpty
    private Integer startRangeIndex;

    @NotEmpty
    private Integer endRangeIndex;

    @NotEmpty
    private Integer poemId;
}
