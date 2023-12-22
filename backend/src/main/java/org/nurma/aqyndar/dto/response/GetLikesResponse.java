package org.nurma.aqyndar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLikesResponse {
    private int poemLikes;
    private int annotationLikes;
    private int poemDislikes;
    private int annotationDislikes;

    public int getLikes() {
        return poemLikes + annotationLikes;
    }

    public int getDislikes() {
        return poemDislikes + annotationDislikes;
    }

    public int getSum() {
        return getLikes() - getDislikes();
    }
}
