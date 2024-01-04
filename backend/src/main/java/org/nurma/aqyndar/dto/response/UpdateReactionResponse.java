package org.nurma.aqyndar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.nurma.aqyndar.entity.enums.ReactionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReactionResponse {
    private final String status = "success";
    private int id;
    private ReactedEntity reactedEntity;
    private int reactedEntityId;
    private int reactionType;
    private int userId;

    public void setReactionTypeFromEnum(final ReactionType reactionTypeEnum) {
        this.reactionType = reactionTypeEnum.getValue();
    }
}
