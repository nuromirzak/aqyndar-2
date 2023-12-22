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
import org.nurma.aqyndar.dto.response.GetWhoResponse;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.nurma.aqyndar.entity.enums.ReactionType;
import org.nurma.aqyndar.entity.enums.TopEntity;
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
    private int authorId;
    private int annotationId;

    @BeforeEach
    void setUp() throws Exception {
        signUp(new SignupRequest(EMAIL, FIRST_NAME, PASSWORD));

        token = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class)
                .getAccessToken();

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

    @Test
    void getTopPoems() throws Exception {
        final int poemsCount = 10;
        List<Integer> poemIds = new ArrayList<>();

        for (int i = 0; i < poemsCount; i++) {
            int id = fromJson(
                    createPoem(new CreatePoemRequest("title" + i, "content" + i, authorId), token),
                    GetPoemResponse.class)
                    .getId();
            poemIds.add(id);
        }

        final int usersCount = 3;
        List<String> tokens = new ArrayList<>();

        for (int i = 0; i < usersCount; i++) {
            SignupRequest signupRequest = new SignupRequest(EMAIL + i, FIRST_NAME, PASSWORD);
            signUp(signupRequest);

            String token = fromJson(
                    signin(new SigninRequest(EMAIL + i, PASSWORD)),
                    JwtResponse.class)
                    .getAccessToken();

            tokens.add(token);
        }

        for (int i = 0; i < 3; i++) {
            UpdateReactionRequest updateReactionRequest =
                    new UpdateReactionRequest(ReactedEntity.POEM.name(), poemIds.get(9), ReactionType.LIKE.getValue());

            updateReaction(updateReactionRequest, tokens.get(i));
        }

        for (int i = 0; i < 2; i++) {
            UpdateReactionRequest updateReactionRequest =
                    new UpdateReactionRequest(ReactedEntity.POEM.name(), poemIds.get(1), ReactionType.LIKE.getValue());

            updateReaction(updateReactionRequest, tokens.get(i));
        }

        for (int i = 0; i < 1; i++) {
            UpdateReactionRequest updateReactionRequest =
                    new UpdateReactionRequest(ReactedEntity.POEM.name(), poemIds.get(5), ReactionType.LIKE.getValue());

            updateReaction(updateReactionRequest, tokens.get(i));
        }

        getTopEntities(TopEntity.POEM.name())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(poemIds.get(9)))
                .andExpect(jsonPath("$[0].name").value("title9"))
                .andExpect(jsonPath("$[0].reactionSum").value(3))
                .andExpect(jsonPath("$[1].id").value(poemIds.get(1)))
                .andExpect(jsonPath("$[1].name").value("title1"))
                .andExpect(jsonPath("$[1].reactionSum").value(2))
                .andExpect(jsonPath("$[2].id").value(poemIds.get(5)))
                .andExpect(jsonPath("$[2].name").value("title5"))
                .andExpect(jsonPath("$[2].reactionSum").value(1));
    }

    @Test
    void getTopUsers() throws Exception {
        final int usersCount = 3;
        List<String> tokens = new ArrayList<>();
        List<Integer> userIds = new ArrayList<>();

        for (int i = 0; i < usersCount; i++) {
            SignupRequest signupRequest = new SignupRequest(EMAIL + i, FIRST_NAME, PASSWORD);
            signUp(signupRequest);
            String token = fromJson(signin(new SigninRequest(EMAIL + i, PASSWORD)), JwtResponse.class).getAccessToken();
            tokens.add(token);
            userIds.add(fromJson(getCurrentUser(token), GetWhoResponse.class).getId());
        }

        List<Integer> poemIds = new ArrayList<>();
        for (int i = 0; i < usersCount; i++) {
            CreatePoemRequest createPoemRequest = new CreatePoemRequest("title" + i, "content" + i, authorId);
            poemIds.add(fromJson(createPoem(createPoemRequest, tokens.get(i)), GetPoemResponse.class).getId());
        }

        // Like 2nd poem 3 times
        for (int i = 0; i < 3; i++) {
            UpdateReactionRequest updateReactionRequest =
                    new UpdateReactionRequest(ReactedEntity.POEM.name(), poemIds.get(1), ReactionType.LIKE.getValue());

            updateReaction(updateReactionRequest, tokens.get(i));
        }

        // Dislike 1st poem 1 time
        for (int i = 0; i < 1; i++) {
            UpdateReactionRequest updateReactionRequest =
                    new UpdateReactionRequest(ReactedEntity.POEM.name(), poemIds.get(0),
                            ReactionType.DISLIKE.getValue());

            updateReaction(updateReactionRequest, tokens.get(i));
        }

        int defaultUserId = fromJson(getCurrentUser(token), GetWhoResponse.class).getId();

        getTopEntities(TopEntity.USER.name())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id").value(userIds.get(1)))
                .andExpect(jsonPath("$[0].name").value(EMAIL + 1))
                .andExpect(jsonPath("$[0].reactionSum").value(3))
                .andExpect(jsonPath("$[1].id").value(defaultUserId))
                .andExpect(jsonPath("$[1].name").value(EMAIL))
                .andExpect(jsonPath("$[1].reactionSum").value(0))
                .andExpect(jsonPath("$[2].id").value(userIds.get(2)))
                .andExpect(jsonPath("$[2].name").value(EMAIL + 2))
                .andExpect(jsonPath("$[2].reactionSum").value(0))
                .andExpect(jsonPath("$[3].id").value(userIds.get(0)))
                .andExpect(jsonPath("$[3].name").value(EMAIL + 0))
                .andExpect(jsonPath("$[3].reactionSum").value(-1));
    }
}