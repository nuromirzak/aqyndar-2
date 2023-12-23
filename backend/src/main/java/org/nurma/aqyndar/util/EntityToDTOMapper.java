package org.nurma.aqyndar.util;

import lombok.experimental.UtilityClass;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.dto.response.GetTopicResponse;
import org.nurma.aqyndar.dto.response.GetWhoResponse;
import org.nurma.aqyndar.entity.Annotation;
import org.nurma.aqyndar.entity.Author;
import org.nurma.aqyndar.entity.Poem;
import org.nurma.aqyndar.entity.Topic;
import org.nurma.aqyndar.entity.User;

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
        getAnnotationResponse.setUserId(annotation.getUser().getId());
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

        getPoemResponse.setSchoolGrade(poem.getSchoolGrade());
        getPoemResponse.setComplexity(poem.getComplexity());
        getPoemResponse.setTopics(poem.getTopics().stream()
                .map(Topic::getName)
                .toList());

        return getPoemResponse;
    }

    public static GetWhoResponse mapUserToGetWhoResponse(final User user) {
        GetWhoResponse getWhoResponse = new GetWhoResponse();
        getWhoResponse.setId(user.getId());
        getWhoResponse.setEmail(user.getEmail());
        getWhoResponse.setFirstName(user.getFirstName());

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
        getWhoResponse.setRoles(roles);

        return getWhoResponse;
    }

    public static GetTopicResponse mapTopicToGetTopicResponse(final Topic topic) {
        GetTopicResponse getTopicResponse = new GetTopicResponse();
        getTopicResponse.setId(topic.getId());
        getTopicResponse.setName(topic.getName());
        return getTopicResponse;
    }
}
