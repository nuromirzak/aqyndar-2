package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.PatchAuthorRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthorControllerTest extends AbstractControllerTest {
    private static final String EMAIL = "steve@gmail.com";
    private static final String FIRST_NAME = "Stevie";
    private static final String PASSWORD = "12345678";
    private static final String AUTHOR_FULL_NAME = "Abai Qunanbaiuly";
    private static final String POEM_TITLE = "Qys";
    private static final String POEM_CONTENT = """
            Aq kiımdı, denelı, aq saqaldy,
            Soqyr mylqau tanymas tırı jandy.
            Üstı-basy aq qyrau tüsı suyq,
            Basqan jerı syqyrlap kelıp qaldy.""";
    private static final String VERY_LONG_STRING = "a".repeat(500);
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
    void getExistingAuthor() throws Exception {
        ResultActions resultActions = createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token);

        GetAuthorResponse getAuthorResponse = fromJson(resultActions, GetAuthorResponse.class);

        int authorId = getAuthorResponse.getId();

        getAuthor(authorId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(jsonPath("$.fullName").value(AUTHOR_FULL_NAME));
    }

    @Test
    void getNonExistingAuthor() throws Exception {
        int nonExistingAuthorId = (int) 1e9;

        getAuthor(nonExistingAuthorId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void createAuthorWithToken() throws Exception {
        createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value(AUTHOR_FULL_NAME));
    }

    @Test
    void createAuthorWithExistingName() throws Exception {
        createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token);

        createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    void createAuthorWithoutToken() throws Exception {
        createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void patchAuthorWithToken() throws Exception {
        int authorId = fromJson(
                createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token),
                GetAuthorResponse.class)
                .getId();

        PatchAuthorRequest patchAuthorRequest = new PatchAuthorRequest("Pushkin");

        updateAuthor(authorId, patchAuthorRequest, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(patchAuthorRequest.getFullName()));
    }

    @Test
    void patchNonExistingAuthor() throws Exception {
        int nonExistingAuthorId = (int) 1e9;

        PatchAuthorRequest patchAuthorRequest = new PatchAuthorRequest("Pushkin");

        updateAuthor(nonExistingAuthorId, patchAuthorRequest, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void patchAuthorWithEmptyPatch() throws Exception {
        int authorId = fromJson(
                createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token),
                GetAuthorResponse.class)
                .getId();

        updateAuthor(authorId, new PatchAuthorRequest(), token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(AUTHOR_FULL_NAME));
    }

    @Test
    void patchAuthorWithoutToken() throws Exception {
        int authorId = fromJson(
                createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token),
                GetAuthorResponse.class)
                .getId();

        PatchAuthorRequest patchAuthorRequest = new PatchAuthorRequest("Pushkin");

        updateAuthor(authorId, patchAuthorRequest, null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void deleteAuthorWithToken() throws Exception {
        int authorId = fromJson(
                createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token),
                GetAuthorResponse.class)
                .getId();

        deleteAuthor(authorId, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void deleteAuthorWithPoem() throws Exception {
        int authorId = fromJson(
                createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token),
                GetAuthorResponse.class)
                .getId();

        createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token);

        deleteAuthor(authorId, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    void deleteNonExistingAuthor() throws Exception {
        int nonExistingAuthorId = (int) 1e9;

        deleteAuthor(nonExistingAuthorId, token)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.NOT_FOUND)));
    }

    @Test
    void deleteAuthorWithoutToken() throws Exception {
        int authorId = fromJson(
                createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token),
                GetAuthorResponse.class)
                .getId();

        deleteAuthor(authorId, null)
                .andExpect(status().isForbidden());
//                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }
}