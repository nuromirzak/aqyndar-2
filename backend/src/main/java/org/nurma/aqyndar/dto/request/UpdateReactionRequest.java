package org.nurma.aqyndar.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReactionRequest {
    @NotEmpty
    private String reactedEntity;

    @NotEmpty
    private int reactedEntityId;

    @NotEmpty
    private int reactionType;
}
