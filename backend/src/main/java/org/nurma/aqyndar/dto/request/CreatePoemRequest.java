package org.nurma.aqyndar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePoemRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private Integer authorId;

    public CreatePoemRequest(final String title, final String content, final int authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    private Integer schoolGrade;
    private Integer complexity;
    private List<String> topics = new ArrayList<>();
}
