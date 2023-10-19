package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.PatchPoemRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
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
    void fllPatchPoemWithToken() throws Exception {
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