package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class PoemControllerTest extends AbstractControllerTest {
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
                .andExpect(jsonPath("$.authorId").value(authorId));
    }

    @Test
    void getPoemWithAnnotations() throws Exception {
        String POEM_CONTENT = """
                Shall I compare [thee to a summer's day?](%d)
                [Thou art more lovely and more temperate:](%d)
                Rough winds do [shake the darling buds of May,](%d)
                And summer's lease hath all too short a date:[Fleeting moments](-1)""";

        String annotation1 = "Nature's beauty compared",
                annotation2 = "Defining the beloved's qualities",
                annotation3 = "Nature's imperfections";

        int annotation1Id = fromJson(createAnnotation(new CreateAnnotationRequest(annotation1), token),
                GetAnnotationResponse.class).getId();
        int annotation2Id = fromJson(createAnnotation(new CreateAnnotationRequest(annotation2), token),
                GetAnnotationResponse.class).getId();
        int annotation3Id = fromJson(createAnnotation(new CreateAnnotationRequest(annotation3), token),
                GetAnnotationResponse.class).getId();

        POEM_CONTENT = String.format(POEM_CONTENT, annotation1Id, annotation2Id, annotation3Id);

        int poemId = fromJson(createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class).getId();

        GetPoemResponse getPoemResponse = fromJson(getPoem(poemId), GetPoemResponse.class);

        assertEquals(poemId, getPoemResponse.getId());
        assertEquals(POEM_TITLE, getPoemResponse.getTitle());
        assertEquals(POEM_CONTENT, getPoemResponse.getContent());
        assertEquals(authorId, getPoemResponse.getAuthorId());

        Map<Integer, GetAnnotationResponse> expectedAnnotations = new HashMap<>();
        expectedAnnotations.put(annotation1Id, new GetAnnotationResponse(annotation1Id, annotation1));
        expectedAnnotations.put(annotation2Id, new GetAnnotationResponse(annotation2Id, annotation2));
        expectedAnnotations.put(annotation3Id, new GetAnnotationResponse(annotation3Id, annotation3));

        assertEquals(expectedAnnotations, getPoemResponse.getAnnotations());
        assertFalse(getPoemResponse.getAnnotations().containsKey(-1));
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

        Map<Integer, GetAnnotationResponse> expectedAnnotations = new HashMap<>();
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
    void createPoemWithToken() throws Exception {
        createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(POEM_TITLE))
                .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(authorId));
    }

    @Test
    void createPoemWithOverlappingAnnotations() throws Exception {
        String POEM_CONTENT = "[This [has [nested](6)](2)](1)";

        createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
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
                .andExpect(jsonPath("$.authorId").value(authorId));
    }

    @Test
    void createPoemWithoutToken() throws Exception {
        createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
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
                .andExpect(jsonPath("$.authorId").value(authorId));
    }

    @Test
    void patchPoemWithOverlappingAnnotations() throws Exception {
        String OVERLAPPING_POEM_CONTENT = "[This [has [nested](6)](2)](1)";

        int poemId = fromJson(
                createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token),
                GetPoemResponse.class)
                .getId();

        PatchPoemRequest patchPoemRequest = PatchPoemRequest.builder()
                .content(OVERLAPPING_POEM_CONTENT)
                .build();

        updatePoem(poemId, patchPoemRequest, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
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
        String newContent = "I'm new content";
        PatchPoemRequest patchPoemRequest = PatchPoemRequest.builder()
                .title(newTitle)
                .content(newContent)
                .authorId(newAuthorId)
                .build();

        updatePoem(poemId, patchPoemRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(poemId))
                .andExpect(jsonPath("$.title").value(newTitle))
                .andExpect(jsonPath("$.content").value(newContent))
                .andExpect(jsonPath("$.authorId").value(newAuthorId));
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
                .andExpect(jsonPath("$.authorId").value(authorId));
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
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
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
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }
}