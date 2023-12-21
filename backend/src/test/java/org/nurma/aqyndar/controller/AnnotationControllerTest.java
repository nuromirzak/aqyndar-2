package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.request.*;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AnnotationControllerTest extends AbstractControllerTest {
    private static final String EMAIL = "steve@gmail.com";
    private static final String FIRST_NAME = "Stevie";
    private static final String PASSWORD = "12345678";
    private static final String ANNOTATION = """
            Phasellus eget dui at nulla sollicitudin malesuada eu sit amet mauris.
            Proin in turpis in risus condimentum efficitur.
            Ut a mauris ac libero dapibus imperdiet.
            Nullam eu ante mattis, imperdiet sem eget, tristique lorem.""";
    private static final String VERY_LONG_STRING = "a".repeat(1000);
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
    }

    @Test
    void getExistingAnnotation() throws Exception {
        ResultActions resultActions = createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token);

        GetAnnotationResponse getAnnotationResponse = fromJson(resultActions, GetAnnotationResponse.class);

        int annotationId = getAnnotationResponse.getId();

        getAnnotation(annotationId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(annotationId))
                .andExpect(jsonPath("$.content").value(ANNOTATION))
                .andExpect(jsonPath("$.startRangeIndex").value(START_RANGE_INDEX))
                .andExpect(jsonPath("$.endRangeIndex").value(END_RANGE_INDEX))
                .andExpect(jsonPath("$.poemId").value(poemId))
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    void getNonExistingAnnotation() throws Exception {
        int nonExistingAnnotationId = (int) 1e9;

        getAnnotation(nonExistingAnnotationId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void createAnnotationWithToken() throws Exception {
        createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value(ANNOTATION))
                .andExpect(jsonPath("$.startRangeIndex").value(START_RANGE_INDEX))
                .andExpect(jsonPath("$.endRangeIndex").value(END_RANGE_INDEX))
                .andExpect(jsonPath("$.poemId").value(poemId))
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    void createLongAnnotationWithToken() throws Exception {
        createAnnotation(new CreateAnnotationRequest(VERY_LONG_STRING, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value(VERY_LONG_STRING))
                .andExpect(jsonPath("$.startRangeIndex").value(START_RANGE_INDEX))
                .andExpect(jsonPath("$.endRangeIndex").value(END_RANGE_INDEX))
                .andExpect(jsonPath("$.poemId").value(poemId))
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    void createAnnotationWithExistingName() throws Exception {
        createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token);

        createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token)
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value(ANNOTATION))
                .andExpect(jsonPath("$.startRangeIndex").value(START_RANGE_INDEX))
                .andExpect(jsonPath("$.endRangeIndex").value(END_RANGE_INDEX))
                .andExpect(jsonPath("$.poemId").value(poemId))
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    void createAnnotationWithoutToken() throws Exception {
        createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void createAnnotationOfNonExistingPoem() throws Exception {
        int nonExistingPoemId = -1;
        createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, nonExistingPoemId), token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void patchAnnotationWithToken() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token),
                GetAnnotationResponse.class)
                .getId();

        int newPoemId = fromJson(
                createPoem(new CreatePoemRequest("New poem", "New content", authorId), token),
                GetPoemResponse.class)
                .getId();

        PatchAnnotationRequest patchAnnotationRequest = new PatchAnnotationRequest();
        patchAnnotationRequest.setContent("I'm new content");
        patchAnnotationRequest.setStartRangeIndex(START_RANGE_INDEX + 1);
        patchAnnotationRequest.setEndRangeIndex(END_RANGE_INDEX + 1);
        patchAnnotationRequest.setPoemId(newPoemId);

        updateAnnotation(annotationId, patchAnnotationRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(patchAnnotationRequest.getContent()))
                .andExpect(jsonPath("$.startRangeIndex").value(patchAnnotationRequest.getStartRangeIndex()))
                .andExpect(jsonPath("$.endRangeIndex").value(patchAnnotationRequest.getEndRangeIndex()))
                .andExpect(jsonPath("$.poemId").value(newPoemId));
    }

    @Test
    void patchNonExistingAnnotation() throws Exception {
        int nonExistingAnnotationId = (int) 1e9;

        PatchAnnotationRequest patchAnnotationRequest = new PatchAnnotationRequest();
        patchAnnotationRequest.setContent("I'm new content");

        updateAnnotation(nonExistingAnnotationId, patchAnnotationRequest, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void patchAnnotationWithNonExistingPoem() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token),
                GetAnnotationResponse.class)
                .getId();

        int nonExistingPoemId = -1;

        PatchAnnotationRequest patchAnnotationRequest = new PatchAnnotationRequest();
        patchAnnotationRequest.setPoemId(nonExistingPoemId);

        updateAnnotation(annotationId, patchAnnotationRequest, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void patchAnnotationWithEmptyPatch() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token),
                GetAnnotationResponse.class)
                .getId();

        updateAnnotation(annotationId, new PatchAnnotationRequest(), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(ANNOTATION))
                .andExpect(jsonPath("$.startRangeIndex").value(START_RANGE_INDEX))
                .andExpect(jsonPath("$.endRangeIndex").value(END_RANGE_INDEX))
                .andExpect(jsonPath("$.poemId").value(poemId));
    }

    @Test
    void patchAnnotationWithoutToken() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token),
                GetAnnotationResponse.class)
                .getId();

        PatchAnnotationRequest patchAnnotationRequest = new PatchAnnotationRequest();
        patchAnnotationRequest.setContent("I'm new content");

        updateAnnotation(annotationId, patchAnnotationRequest, null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void deleteAnnotationWithToken() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token),
                GetAnnotationResponse.class)
                .getId();

        deleteAnnotation(annotationId, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void deleteNonExistingAnnotation() throws Exception {
        int nonExistingAnnotationId = (int) 1e9;

        deleteAnnotation(nonExistingAnnotationId, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void deleteAnnotationWithoutToken() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION, START_RANGE_INDEX, END_RANGE_INDEX, poemId), token),
                GetAnnotationResponse.class)
                .getId();

        deleteAnnotation(annotationId, null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }
}