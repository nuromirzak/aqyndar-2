package org.nurma.aqyndar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPoemResponse {
    private int id;
    private String title;
    private String content;
    private int authorId;
    private String authorName;
    private List<GetAnnotationResponse> annotations;
    private int userId;
    private Integer schoolGrade;
    private Integer complexity;
    private List<String> topics;
}
