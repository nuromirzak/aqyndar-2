package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.request.CreateAnnotationRequest;
import org.nurma.aqyndar.dto.request.PatchAnnotationRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
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
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        signUp(new SignupRequest(EMAIL, FIRST_NAME, PASSWORD));

        token = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class)
                .getAccessToken();
    }

    @Test
    void getExistingAnnotation() throws Exception {
        ResultActions resultActions = createAnnotation(new CreateAnnotationRequest(ANNOTATION), token);

        GetAnnotationResponse getAnnotationResponse = fromJson(resultActions, GetAnnotationResponse.class);

        int annotationId = getAnnotationResponse.getId();

        getAnnotation(annotationId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(annotationId))
                .andExpect(jsonPath("$.content").value(ANNOTATION));
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
        createAnnotation(new CreateAnnotationRequest(ANNOTATION), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value(ANNOTATION));
    }

    @Test
    void createLongAnnotationWithToken() throws Exception {
        createAnnotation(new CreateAnnotationRequest(VERY_LONG_STRING), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value(VERY_LONG_STRING));
    }

    @Test
    void createAnnotationWithExistingName() throws Exception {
        createAnnotation(new CreateAnnotationRequest(ANNOTATION), token);

        createAnnotation(new CreateAnnotationRequest(ANNOTATION), token)
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value(ANNOTATION));
    }

    @Test
    void createAnnotationWithoutToken() throws Exception {
        createAnnotation(new CreateAnnotationRequest(ANNOTATION), null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void patchAnnotationWithToken() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION), token),
                GetAnnotationResponse.class)
                .getId();

        PatchAnnotationRequest patchAnnotationRequest = new PatchAnnotationRequest("I'm new content");

        updateAnnotation(annotationId, patchAnnotationRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(patchAnnotationRequest.getContent()));
    }

    @Test
    void patchNonExistingAnnotation() throws Exception {
        int nonExistingAnnotationId = (int) 1e9;

        PatchAnnotationRequest patchAnnotationRequest = new PatchAnnotationRequest("I'm new content");

        updateAnnotation(nonExistingAnnotationId, patchAnnotationRequest, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void patchAnnotationWithEmptyPatch() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION), token),
                GetAnnotationResponse.class)
                .getId();

        updateAnnotation(annotationId, new PatchAnnotationRequest(), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(ANNOTATION));
    }

    @Test
    void patchAnnotationWithoutToken() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION), token),
                GetAnnotationResponse.class)
                .getId();

        PatchAnnotationRequest patchAnnotationRequest = new PatchAnnotationRequest("I'm new content");

        updateAnnotation(annotationId, patchAnnotationRequest, null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void deleteAnnotationWithToken() throws Exception {
        int annotationId = fromJson(
                createAnnotation(new CreateAnnotationRequest(ANNOTATION), token),
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
                createAnnotation(new CreateAnnotationRequest(ANNOTATION), token),
                GetAnnotationResponse.class)
                .getId();

        deleteAnnotation(annotationId, null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }
}