package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nurma.aqyndar.configuration.TestDataFactory;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.request.CreateAnnotationRequest;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.request.UpdateReactionRequest;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.dto.response.GetWhoResponse;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.nurma.aqyndar.entity.enums.ReactionType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class ProfileControllerTest extends TestDataFactory {
    private static final String EMAIL = "steve@gmail.com";
    private static final String FIRST_NAME = "Stevie";
    private static final String PASSWORD = "12345678";
    private static final String ANNOTATION = """
            Phasellus eget dui at nulla sollicitudin malesuada eu sit amet mauris.
            Proin in turpis in risus condimentum efficitur.
            Ut a mauris ac libero dapibus imperdiet.
            Nullam eu ante mattis, imperdiet sem eget, tristique lorem.""";
    private static final String AUTHOR_FULL_NAME = "Abai Qunanbaiuly";
    private static final String POEM_TITLE = "Qys";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String POEM_CONTENT = """
            Aq kiımdı, denelı, aq saqaldy,
            Soqyr mylqau tanymas tırı jandy.
            Üstı-basy aq qyrau tüsı suyq,
            Basqan jerı syqyrlap kelıp qaldy.""";
    private static final int START_RANGE_INDEX = 0, END_RANGE_INDEX = 10;
    private String token;
    private int poemId;
    private int authorId;
    private int annotationId;
    private int userId;

    @BeforeEach
    void setUp() throws Exception {
        signUp(new SignupRequest(EMAIL, FIRST_NAME, PASSWORD));

        token = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class)
                .getAccessToken();

        userId = fromJson(
                getCurrentUser(token),
                GetWhoResponse.class)
                .getId();

        authorId = fromJson(
                createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token),
                GetAuthorResponse.class)
                .getId();

        poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId),
                        token),
                GetAnnotationResponse.class)
                .getId();
    }

    @Test
    void who_with_anonymous() throws Exception {
        getCurrentUser(null)
                .andExpect(status().isForbidden());
    }

    @Test
    void who_with_user() throws Exception {
        getCurrentUser(token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    @Disabled("Implement in the future")
    void who_with_deleted_user() throws Exception {
        deleteAccount(token);

        getCurrentUser(token)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void who_with_user_by_id() throws Exception {
        getUser(userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    void who_non_existing_user() throws Exception {
        getUser(100)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void testPointsWithLikes() throws Exception {
        for (int i = 0; i < 5; i++) {
            UpdateReactionRequest updateReactionRequest = new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId,
                    ReactionType.LIKE.getValue());

            updateReactionWithRandomUser(updateReactionRequest);
        }

        getLikes(userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poemLikes").value(5))
                .andExpect(jsonPath("$.poemDislikes").value(0))
                .andExpect(jsonPath("$.annotationLikes").value(0))
                .andExpect(jsonPath("$.annotationDislikes").value(0));
    }

    @Test
    void testPointsWithLikesWithDifferentEntity() throws Exception {
        for (int i = 0; i < 6; i++) {
            UpdateReactionRequest updateReactionRequest;

            if (i % 2 == 0) {
                updateReactionRequest = new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId,
                        ReactionType.LIKE.getValue());
            } else {
                updateReactionRequest = new UpdateReactionRequest(ReactedEntity.ANNOTATION.name(), annotationId,
                        ReactionType.LIKE.getValue());
            }

            updateReactionWithRandomUser(updateReactionRequest);
        }

        getLikes(userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poemLikes").value(3))
                .andExpect(jsonPath("$.poemDislikes").value(0))
                .andExpect(jsonPath("$.annotationLikes").value(3))
                .andExpect(jsonPath("$.annotationDislikes").value(0));
    }

    @Test
    void testPointsWithDislikes() throws Exception {
        for (int i = 0; i < 5; i++) {
            UpdateReactionRequest updateReactionRequest = new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId,
                    ReactionType.DISLIKE.getValue());

            updateReactionWithRandomUser(updateReactionRequest);
        }

        getLikes(userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poemLikes").value(0))
                .andExpect(jsonPath("$.poemDislikes").value(5))
                .andExpect(jsonPath("$.annotationLikes").value(0))
                .andExpect(jsonPath("$.annotationDislikes").value(0));
    }

    @Test
    void testPointsWithLikesAndDislikes() throws Exception {
        for (int i = 0; i < 6; i++) {
            UpdateReactionRequest updateReactionRequest;

            if (i % 2 == 0) {
                updateReactionRequest = new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId,
                        ReactionType.LIKE.getValue());
            } else {
                updateReactionRequest = new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId,
                        ReactionType.DISLIKE.getValue());
            }

            updateReactionWithRandomUser(updateReactionRequest);
        }

        getLikes(userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poemLikes").value(3))
                .andExpect(jsonPath("$.poemDislikes").value(3))
                .andExpect(jsonPath("$.annotationLikes").value(0))
                .andExpect(jsonPath("$.annotationDislikes").value(0));
    }

    @Test
    void testPointsWithLikesAndDislikesEveryEntity() throws Exception {
        List<UpdateReactionRequest> updateReactionRequests = List.of(
                new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, ReactionType.LIKE.getValue()),
                new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, ReactionType.DISLIKE.getValue()),
                new UpdateReactionRequest(ReactedEntity.ANNOTATION.name(), annotationId, ReactionType.LIKE.getValue()),
                new UpdateReactionRequest(ReactedEntity.ANNOTATION.name(), annotationId,
                        ReactionType.DISLIKE.getValue())
        );

        for (int i = 0; i < 4; i++) {
            updateReactionWithRandomUser(updateReactionRequests.get(i));
        }

        getLikes(userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poemLikes").value(1))
                .andExpect(jsonPath("$.poemDislikes").value(1))
                .andExpect(jsonPath("$.annotationLikes").value(1))
                .andExpect(jsonPath("$.annotationDislikes").value(1));
    }
}