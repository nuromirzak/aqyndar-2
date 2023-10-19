package org.nurma.aqyndar.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
