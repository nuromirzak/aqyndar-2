package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.nurma.aqyndar.entity.enums.ReactionType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class ReactionControllerTest extends AbstractControllerTest {
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
    private int annotationId;

    @BeforeEach
    void setUp() throws Exception {
        signUp(new SignupRequest(EMAIL, FIRST_NAME, PASSWORD));

        token = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class)
                .getAccessToken();

        int authorId = fromJson(
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
    void putReactionOnPoem() throws Exception {
        UpdateReactionRequest updateReactionRequest =
                new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, ReactionType.LIKE.getValue());

        updateReaction(updateReactionRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reactionType").value(updateReactionRequest.getReactionType()))
                .andExpect(jsonPath("$.reactedEntity").value(updateReactionRequest.getReactedEntity()))
                .andExpect(jsonPath("$.reactedEntityId").value(updateReactionRequest.getReactedEntityId()))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void putReactionOnAnnotation() throws Exception {
        UpdateReactionRequest updateReactionRequest =
                new UpdateReactionRequest(ReactedEntity.ANNOTATION.name(), annotationId, ReactionType.LIKE.getValue());

        updateReaction(updateReactionRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reactionType").value(updateReactionRequest.getReactionType()))
                .andExpect(jsonPath("$.reactedEntity").value(updateReactionRequest.getReactedEntity()))
                .andExpect(jsonPath("$.reactedEntityId").value(updateReactionRequest.getReactedEntityId()))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void countLike() throws Exception {
        UpdateReactionRequest updateReactionRequest =
                new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, ReactionType.LIKE.getValue());

        updateReaction(updateReactionRequest, token);

        getReaction(ReactedEntity.POEM.name(), poemId, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.dislikes").value(0));
    }

    @Test
    void likeAndDislikeOnTheSameEntity() throws Exception {
        UpdateReactionRequest updateReactionRequest =
                new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, ReactionType.LIKE.getValue());

        updateReaction(updateReactionRequest, token);

        updateReactionRequest.setReactionType(ReactionType.DISLIKE.getValue());

        updateReaction(updateReactionRequest, token);

        getReaction(ReactedEntity.POEM.name(), poemId, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.dislikes").value(1));
    }


    @Test
    void multipleLikes() throws Exception {
        UpdateReactionRequest updateReactionRequest =
                new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, ReactionType.LIKE.getValue());

        for (int i = 0; i < 10; i++) {
            updateReaction(updateReactionRequest, token);
        }

        getReaction(ReactedEntity.POEM.name(), poemId, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.dislikes").value(0));
    }

    @Test
    void putReactionOnNonExistingEntity() throws Exception {
        UpdateReactionRequest updateReactionRequest =
                new UpdateReactionRequest(ReactedEntity.POEM.name(), 100, ReactionType.LIKE.getValue());

        updateReaction(updateReactionRequest, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));

        UpdateReactionRequest updateReactionRequest2 =
                new UpdateReactionRequest(ReactedEntity.ANNOTATION.name(), 100, ReactionType.LIKE.getValue());

        updateReaction(updateReactionRequest2, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void putReactionWithoutToken() throws Exception {
        UpdateReactionRequest updateReactionRequest =
                new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, ReactionType.LIKE.getValue());

        updateReaction(updateReactionRequest, null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void putReactionWithInvalidReactedEntity() throws Exception {
        UpdateReactionRequest updateReactionRequest =
                new UpdateReactionRequest("invalid", poemId, ReactionType.LIKE.getValue());

        updateReaction(updateReactionRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    void putReactionWithInvalidReactionType() throws Exception {
        UpdateReactionRequest updateReactionRequest =
                new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, 100);

        updateReaction(updateReactionRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    @Disabled("My code right now cannot handle concurrent requests")
    void concurrentReactions() throws Exception {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    SignupRequest signupRequest = new SignupRequest(EMAIL + finalI, FIRST_NAME, PASSWORD);
                    signUp(signupRequest);

                    String token = fromJson(
                            signin(new SigninRequest(EMAIL + finalI, PASSWORD)),
                            JwtResponse.class)
                            .getAccessToken();

                    UpdateReactionRequest localUpdateReactionRequest =
                            new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, ReactionType.LIKE.getValue());

                    updateReaction(localUpdateReactionRequest, token);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Thread.sleep(10000);

        assertTrue(exceptions.isEmpty(), "Errors occurred in concurrent processing");

        getReaction(ReactedEntity.POEM.name(), poemId, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(numberOfThreads))
                .andExpect(jsonPath("$.dislikes").value(0));
    }

    @Test
    void iterativeReactions() throws Exception {
        int numberOfIterations = 10;
        List<Exception> exceptions = new ArrayList<>();

        for (int i = 0; i < numberOfIterations; i++) {
            try {
                SignupRequest signupRequest = new SignupRequest(EMAIL + i, FIRST_NAME, PASSWORD);
                signUp(signupRequest);

                String token = fromJson(
                        signin(new SigninRequest(EMAIL + i, PASSWORD)),
                        JwtResponse.class)
                        .getAccessToken();

                UpdateReactionRequest updateReactionRequest =
                        new UpdateReactionRequest(ReactedEntity.POEM.name(), poemId, ReactionType.LIKE.getValue());

                updateReaction(updateReactionRequest, token);
            } catch (Exception e) {
                exceptions.add(e);
            }
        }

        assertTrue(exceptions.isEmpty(), "Errors occurred during processing");

        getReaction(ReactedEntity.POEM.name(), poemId, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(numberOfIterations))
                .andExpect(jsonPath("$.dislikes").value(0));
    }
}