package org.nurma.aqyndar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAnnotationResponse {
    private int id;
    private String content;
    private int startRangeIndex;
    private int endRangeIndex;
    private int poemId;
    private int userId;
}
