package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nurma.aqyndar.configuration.AbstractController;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.request.CreateAnnotationRequest;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.PatchPoemRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.dto.response.GetWhoResponse;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.nurma.aqyndar.service.PoemService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class PoemControllerTest extends AbstractController {
    private static final String EMAIL = "steve@gmail.com";
    private static final String FIRST_NAME = "Stevie";
    private static final String PASSWORD = "12345678";
    private static final String AUTHOR_FULL_NAME = "Abai Qunanbaiuly";
    private static final String POEM_TITLE = "Qys";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String POEM_CONTENT = """
            Aq kiımdı, denelı, aq saqaldy,
            Soqyr mylqau tanymas tırı jandy.
            Üstı-basy aq qyrau tüsı suyq,
            Basqan jerı syqyrlap kelıp qaldy.""";
    private static final String VERY_LONG_STRING = "a".repeat(1000);
    private String token;
    private int authorId;
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

        int poemsCount = 100;

        for (int i = 0; i < poemsCount; i++) {
            String poemTitle = "Poem #%d".formatted(i + 1);
            String poemContent = "Content #%d".formatted(i + 1);
            createPoem(new CreatePoemRequest(poemTitle, poemContent, authorId), token);
        }
    }

    @Test
    void defaultPaginationLimit() throws Exception {
        getPoems(null, null, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.content[0].title").value("Poem #1"))
                .andExpect(jsonPath("$.content[19].title").value("Poem #20"));
    }

    @Test
    void testPagination() throws Exception {
        int page = 1;
        int size = 10;

        getPoems(page, size, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(size))
                .andExpect(jsonPath("$.content[0].title").value("Poem #11"))
                .andExpect(jsonPath("$.content[9].title").value("Poem #20"));
    }

    @Test
    void testPaginationSorting() throws Exception {
        int page = 0;
        int size = 10;

        String sort = "title,desc";
        getPoems(page, size, sort)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(size))
                .andExpect(jsonPath("$.content[0].title").value("Poem #99"));
    }

    @Test
    void testPaginationOutOfBounds() throws Exception {
        int page = 50;
        int size = 10;

        getPoems(page, size, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @Disabled
    void testInvalidPaginationParameters() throws Exception {
        getPoems(-1, -10, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void getExistingPoem() throws Exception {
        ResultActions resultActions = createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token);

        GetPoemResponse getPoemResponse = fromJson(resultActions, GetPoemResponse.class);

        int poemId = getPoemResponse.getId();

        getPoem(poemId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(poemId))
                .andExpect(jsonPath("$.title").value(POEM_TITLE))
                .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.authorName").value(AUTHOR_FULL_NAME))
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    void getPoemWithAnnotations() throws Exception {
        String POEM_CONTENT = """
                Shall I compare thee to a summer's day?
                Thou art more lovely and more temperate.
                Rough winds do [shake the darling buds of May,
                And summer's lease hath all too short a date.""";

        int poemId = fromJson(createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class).getId();

        CreateAnnotationRequest annotation1 = new CreateAnnotationRequest("Nature's beauty compared", 0, 5, poemId),
                annotation2 = new CreateAnnotationRequest("Defining the beloved's qualities", 35, 40, poemId),
                annotation3 = new CreateAnnotationRequest("Nature's imperfections", 70, 75, poemId);

        int annotation1Id = fromJson(createAnnotation(annotation1, token),
                GetAnnotationResponse.class).getId();
        int annotation2Id = fromJson(createAnnotation(annotation2, token),
                GetAnnotationResponse.class).getId();
        int annotation3Id = fromJson(createAnnotation(annotation3, token),
                GetAnnotationResponse.class).getId();

        GetPoemResponse getPoemResponse = fromJson(getPoem(poemId), GetPoemResponse.class);

        System.out.printf("getPoemResponse=%s\n", getPoemResponse);

        assertEquals(poemId, getPoemResponse.getId());
        assertEquals(POEM_TITLE, getPoemResponse.getTitle());
        assertEquals(POEM_CONTENT, getPoemResponse.getContent());
        assertEquals(authorId, getPoemResponse.getAuthorId());

        List<GetAnnotationResponse> expectedAnnotations = new ArrayList<>();
        expectedAnnotations.add(
                new GetAnnotationResponse(annotation1Id, annotation1.getContent(), annotation1.getStartRangeIndex(),
                        annotation1.getEndRangeIndex(), poemId, userId));
        expectedAnnotations.add(
                new GetAnnotationResponse(annotation2Id, annotation2.getContent(), annotation2.getStartRangeIndex(),
                        annotation2.getEndRangeIndex(), poemId, userId));
        expectedAnnotations.add(
                new GetAnnotationResponse(annotation3Id, annotation3.getContent(), annotation3.getStartRangeIndex(),
                        annotation3.getEndRangeIndex(), poemId, userId));

        assertEquals(expectedAnnotations, getPoemResponse.getAnnotations());
    }

    @Test
    void getPoemWithNonExistingAnnotations() throws Exception {
        int[] nonExistingIds = {(int) 1e9 + 1, (int) 1e9 + 2, (int) 1e9 + 3};
        String POEM_CONTENT =
                "[lorem[%d], ipsum[%d], dolor[%d]]".formatted(nonExistingIds[0], nonExistingIds[1], nonExistingIds[2]);

        int poemId = fromJson(createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class).getId();

        GetPoemResponse getPoemResponse = fromJson(getPoem(poemId), GetPoemResponse.class);

        assertEquals(poemId, getPoemResponse.getId());
        assertEquals(POEM_TITLE, getPoemResponse.getTitle());
        assertEquals(POEM_CONTENT, getPoemResponse.getContent());
        assertEquals(authorId, getPoemResponse.getAuthorId());

        List<GetAnnotationResponse> expectedAnnotations = new ArrayList<>();
        assertEquals(expectedAnnotations, getPoemResponse.getAnnotations());
    }

    @Test
    void getNonExistingPoem() throws Exception {
        int nonExistingAuthorId = (int) 1e9;

        getPoem(nonExistingAuthorId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void createMinimalPoemWithToken() throws Exception {
        createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(POEM_TITLE))
                .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.authorName").value(AUTHOR_FULL_NAME))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.schoolGrade").isEmpty())
                .andExpect(jsonPath("$.complexity").isEmpty())
                .andExpect(jsonPath("$.topics.length()").value(0));
    }

    @Test
    void createPoemWithEmptyFields() throws Exception {
        createPoem(new CreatePoemRequest(POEM_TITLE, "", authorId), token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));

        createPoem(new CreatePoemRequest("", POEM_CONTENT, authorId), token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    void createPoemWithToken() throws Exception {
        final int grade = 10;
        final int complexity = 6;
        final List<String> topics = List.of("topic1", "topic2", "topic3");
        CreatePoemRequest createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, grade, complexity, topics);

        createPoem(createPoemRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(POEM_TITLE))
                .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.authorName").value(AUTHOR_FULL_NAME))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.schoolGrade").value(grade))
                .andExpect(jsonPath("$.complexity").value(complexity))
                .andExpect(jsonPath("$.topics.length()").value(topics.size()))
                .andExpect(jsonPath("$.topics[0]").value(topics.get(0)))
                .andExpect(jsonPath("$.topics[1]").value(topics.get(1)))
                .andExpect(jsonPath("$.topics[2]").value(topics.get(2)));
    }

    @Test
    void createPoemWithInvalidGradeAndComplexity() throws Exception {
        final int gradeAboveMax = PoemService.MAX_GRADE + 1;
        final Integer complexity = null;
        final List<String> topics = List.of();
        CreatePoemRequest createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, gradeAboveMax, complexity, topics);

        createPoem(createPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));

        final int gradeBelowMin = PoemService.MIN_GRADE - 1;
        createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, gradeBelowMin, complexity, topics);
        createPoem(createPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));

        final int complexityAboveMax = PoemService.MAX_COMPLEXITY + 1;
        createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, complexity, complexityAboveMax, topics);
        createPoem(createPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));

        final int complexityBelowMin = PoemService.MIN_COMPLEXITY - 1;
        createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, complexity, complexityBelowMin, topics);
        createPoem(createPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    void createPoemWithInvalidComplexity() throws Exception {
        final int grade = 10;
        final int exceedingComplexity = 11;
        final List<String> topics = List.of();
        CreatePoemRequest createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, grade, exceedingComplexity, topics);

        createPoem(createPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));

        final int minimumComplexity = 0;
        createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, grade, minimumComplexity, topics);
        createPoem(createPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    void createPoemWithRepeatingTopics() throws Exception {
        final Integer grade = null;
        final Integer complexity = null;
        final List<String> topics = List.of("topic1", "topic1", "topic1", "topic2", "topic3");
        CreatePoemRequest createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, grade, complexity, topics);

        createPoem(createPoemRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(POEM_TITLE))
                .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.authorName").value(AUTHOR_FULL_NAME))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.schoolGrade").isEmpty())
                .andExpect(jsonPath("$.complexity").isEmpty())
                .andExpect(jsonPath("$.topics.length()").value(3))
                .andExpect(jsonPath("$.topics[0]").value(topics.get(0)))
                .andExpect(jsonPath("$.topics[1]").value(topics.get(3)))
                .andExpect(jsonPath("$.topics[2]").value(topics.get(4)));

        getAllTopics()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value(topics.get(0)))
                .andExpect(jsonPath("$[1].name").value(topics.get(3)))
                .andExpect(jsonPath("$[2].name").value(topics.get(4)));
    }

    @Test
    void createPoemWithNonExistingAuthor() throws Exception {
        int nonExistingAuthorId = (int) 1e9;

        createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, nonExistingAuthorId), token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void createPoemWithVeryLongContent() throws Exception {
        createPoem(new CreatePoemRequest(POEM_TITLE, VERY_LONG_STRING, authorId), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(POEM_TITLE))
                .andExpect(jsonPath("$.content").value(VERY_LONG_STRING))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.authorName").value(AUTHOR_FULL_NAME))
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    void createPoemWithoutToken() throws Exception {
        createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), null)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void patchPoemWithToken() throws Exception {
        int poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        String newTitle = "I'm new title";
        PatchPoemRequest patchPoemRequest = PatchPoemRequest.builder()
                .title(newTitle)
                .build();

        updatePoem(poemId, patchPoemRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(poemId))
                .andExpect(jsonPath("$.title").value(newTitle))
                .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.authorName").value(AUTHOR_FULL_NAME));
    }

    @Test
    void fullPatchPoemWithToken() throws Exception {
        int newAuthorId = fromJson(
                createAuthor(new CreateAuthorRequest("Pushkin"), token),
                GetAuthorResponse.class)
                .getId();

        int poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        String newTitle = "I'm new title";
        Integer newSchoolGrade = 10;
        Integer newComplexity = 5;
        List<String> newTopics = List.of("topic1", "topic2", "topic3");
        PatchPoemRequest patchPoemRequest = PatchPoemRequest.builder()
                .title(newTitle)
                .authorId(newAuthorId)
                .schoolGrade(newSchoolGrade)
                .complexity(newComplexity)
                .topics(newTopics)
                .build();

        updatePoem(poemId, patchPoemRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(poemId))
                .andExpect(jsonPath("$.title").value(newTitle))
                .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(newAuthorId))
                .andExpect(jsonPath("$.authorName").value("Pushkin"))
                .andExpect(jsonPath("$.schoolGrade").value(newSchoolGrade))
                .andExpect(jsonPath("$.complexity").value(newComplexity))
                .andExpect(jsonPath("$.topics.length()").value(newTopics.size()))
                .andExpect(jsonPath("$.topics[0]").value(newTopics.get(0)))
                .andExpect(jsonPath("$.topics[1]").value(newTopics.get(1)))
                .andExpect(jsonPath("$.topics[2]").value(newTopics.get(2)));
    }

    @Test
    void patchWithExistingTopics() throws Exception {
        Integer newSchoolGrade = 12;
        Integer newComplexity = 1;
        List<String> topics = List.of("topic1");
        CreatePoemRequest createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, newSchoolGrade, newComplexity, topics);

        int poemId = fromJson(createPoem(createPoemRequest, token), GetPoemResponse.class).getId();

        String newTitle = "I'm new title";
        List<String> newTopics = new ArrayList<>(topics);
        PatchPoemRequest patchPoemRequest = PatchPoemRequest.builder()
                .title(newTitle)
                .topics(newTopics)
                .build();

        updatePoem(poemId, patchPoemRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(poemId))
                .andExpect(jsonPath("$.title").value(newTitle))
                .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.authorName").value(AUTHOR_FULL_NAME))
                .andExpect(jsonPath("$.schoolGrade").value(newSchoolGrade))
                .andExpect(jsonPath("$.complexity").value(newComplexity))
                .andExpect(jsonPath("$.topics.length()").value(newTopics.size()))
                .andExpect(jsonPath("$.topics[0]").value(newTopics.get(0)));

        getAllTopics()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(newTopics.get(0)));
    }

    @Test
    void patchPoemWithNonExistingAuthor() throws Exception {
        int poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        int nonExistingAuthorId = (int) 1e9;

        updatePoem(poemId, PatchPoemRequest.builder().authorId(nonExistingAuthorId).build(), token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void patchNonExistingPoem() throws Exception {
        int nonExistingPoemId = (int) 1e9;
        PatchPoemRequest patchPoemRequest = PatchPoemRequest.builder()
                .title("I'm new title")
                .build();

        updatePoem(nonExistingPoemId, patchPoemRequest, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void patchPoemWithInvalidGradeAndComplexity() throws Exception {
        final int gradeAboveMax = PoemService.MAX_GRADE + 1;
        final Integer complexity = null;
        final List<String> topics = List.of();
        PatchPoemRequest patchPoemRequest = PatchPoemRequest.builder()
                .schoolGrade(gradeAboveMax)
                .complexity(complexity)
                .topics(topics)
                .build();

        int poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        updatePoem(poemId, patchPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));

        final int gradeBelowMin = PoemService.MIN_GRADE - 1;
        patchPoemRequest = PatchPoemRequest.builder()
                .schoolGrade(gradeBelowMin)
                .complexity(complexity)
                .topics(topics)
                .build();

        updatePoem(poemId, patchPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));

        final int complexityAboveMax = PoemService.MAX_COMPLEXITY + 1;
        patchPoemRequest = PatchPoemRequest.builder()
                .schoolGrade(complexityAboveMax)
                .complexity(complexityAboveMax)
                .topics(topics)
                .build();

        updatePoem(poemId, patchPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));

        final int complexityBelowMin = PoemService.MIN_COMPLEXITY - 1;
        patchPoemRequest = PatchPoemRequest.builder()
                .schoolGrade(complexityBelowMin)
                .complexity(complexityBelowMin)
                .topics(topics)
                .build();

        updatePoem(poemId, patchPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    void patchPoemWithEmptyPatch() throws Exception {
        int poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        updatePoem(poemId, new PatchPoemRequest(), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(poemId))
                .andExpect(jsonPath("$.title").value(POEM_TITLE))
                .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(authorId))
                .andExpect(jsonPath("$.authorName").value(AUTHOR_FULL_NAME));
    }

    @Test
    void patchPoemWithoutToken() throws Exception {
        int poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        String newTitle = "I'm new title";
        PatchPoemRequest patchPoemRequest = PatchPoemRequest.builder()
                .title(newTitle)
                .build();

        updatePoem(poemId, patchPoemRequest, null)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void deletePoemWithToken() throws Exception {
        int poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        deletePoem(poemId, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void deleteNonExistingPoem() throws Exception {
        int nonExistingPoemId = (int) 1e9;

        deletePoem(nonExistingPoemId, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void deletePoemWithoutToken() throws Exception {
        int poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        deletePoem(poemId, null)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void getTopics() throws Exception {
        getAllTopics()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTopicsWithPoems() throws Exception {
        Integer grade = 10;
        Integer complexity = 5;
        List<String> topics = List.of("topic1", "topic2", "topic3");
        CreatePoemRequest createPoemRequest = new CreatePoemRequest(
                POEM_TITLE, POEM_CONTENT, authorId, grade, complexity, topics);

        createPoem(createPoemRequest, token);

        getAllTopics()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(topics.size()))
                .andExpect(jsonPath("$[0].name").value(topics.get(0)))
                .andExpect(jsonPath("$[1].name").value(topics.get(1)))
                .andExpect(jsonPath("$[2].name").value(topics.get(2)));
    }
}