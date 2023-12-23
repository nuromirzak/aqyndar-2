package org.nurma.aqyndar.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchPoemRequest {
    private String title;
    private Integer authorId;
    private Integer schoolGrade;
    private Integer complexity;
    @Builder.Default
    private List<String> topics = new ArrayList<>();
}
