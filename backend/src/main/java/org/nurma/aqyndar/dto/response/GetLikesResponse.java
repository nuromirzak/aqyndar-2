package org.nurma.aqyndar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLikesResponse {
    private Integer poemLikes;
    private Integer annotationLikes;
    private Integer poemDislikes;
    private Integer annotationDislikes;
}
