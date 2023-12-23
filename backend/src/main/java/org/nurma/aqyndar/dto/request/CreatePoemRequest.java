package org.nurma.aqyndar.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePoemRequest {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    @NotEmpty
    private int authorId;

    public CreatePoemRequest(final String title, final String content, final int authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    private Integer schoolGrade;
    private Integer complexity;
    private List<String> topics = new ArrayList<>();
}
