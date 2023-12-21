package org.nurma.aqyndar.util;

import lombok.experimental.UtilityClass;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.entity.Annotation;
import org.nurma.aqyndar.entity.Author;
import org.nurma.aqyndar.entity.Poem;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class EntityToDTOMapper {
    public static GetAuthorResponse mapAuthorToGetAuthorResponse(final Author author) {
        GetAuthorResponse getAuthorResponse = new GetAuthorResponse();
        getAuthorResponse.setId(author.getId());
        getAuthorResponse.setFullName(author.getFullName());
        getAuthorResponse.setUserId(author.getUser().getId());
        return getAuthorResponse;
    }

    public static GetAnnotationResponse mapAnnotationToGetAnnotationResponse(final Annotation annotation) {
        GetAnnotationResponse getAnnotationResponse = new GetAnnotationResponse();
        getAnnotationResponse.setId(annotation.getId());
        getAnnotationResponse.setContent(annotation.getContent());
        getAnnotationResponse.setStartRangeIndex(annotation.getStartRangeIndex());
        getAnnotationResponse.setEndRangeIndex(annotation.getEndRangeIndex());
        getAnnotationResponse.setPoemId(annotation.getPoem().getId());
        return getAnnotationResponse;
    }

    public static GetPoemResponse mapPoemToGetPoemResponse(final Poem poem) {
        GetPoemResponse getPoemResponse = new GetPoemResponse();
        getPoemResponse.setId(poem.getId());
        getPoemResponse.setTitle(poem.getTitle());
        getPoemResponse.setContent(poem.getContent());
        getPoemResponse.setAuthorId(poem.getAuthor().getId());

        List<GetAnnotationResponse> getAnnotationResponses = new ArrayList<>();

        for (Annotation annotation : poem.getAnnotations()) {
            GetAnnotationResponse getAnnotationResponse = mapAnnotationToGetAnnotationResponse(annotation);
            getAnnotationResponses.add(getAnnotationResponse);
        }

        getPoemResponse.setAnnotations(getAnnotationResponses);

        getPoemResponse.setUserId(poem.getUser().getId());

        return getPoemResponse;
    }
}
