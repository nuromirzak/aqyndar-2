package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nurma.aqyndar.configuration.AbstractController;
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
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthorControllerTest extends AbstractController {
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

        int authorsCount = 100;

        for (int i = 0; i < authorsCount; i++) {
            String authorFullName = "Author #%d".formatted(i + 1);
            createAuthor(new CreateAuthorRequest(authorFullName), token);
        }
    }

    @Test
    void defaultPaginationLimit() throws Exception {
        getAuthors(null, null, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.content[0].fullName").value("Author #1"))
                .andExpect(jsonPath("$.content[19].fullName").value("Author #20"));
    }

    @Test
    void testPagination() throws Exception {
        int page = 1;
        int size = 10;

        getAuthors(page, size, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(size))
                .andExpect(jsonPath("$.content[0].fullName").value("Author #11"))
                .andExpect(jsonPath("$.content[9].fullName").value("Author #20"));
    }

    @Test
    void testPaginationSorting() throws Exception {
        int page = 0;
        int size = 10;

        String sort = "fullName,desc";
        getAuthors(page, size, sort)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(size))
                .andExpect(jsonPath("$.content[0].fullName").value("Author #99"));
    }

    @Test
    void testPaginationOutOfBounds() throws Exception {
        int page = 50;
        int size = 10;

        getAuthors(page, size, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @Disabled
    void testInvalidPaginationParameters() throws Exception {
        getAuthors(-1, -10, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getExistingAuthor() throws Exception {
        ResultActions resultActions = createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token);

        GetAuthorResponse getAuthorResponse = fromJson(resultActions, GetAuthorResponse.class);

        int authorId = getAuthorResponse.getId();

        getAuthor(authorId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(jsonPath("$.fullName").value(AUTHOR_FULL_NAME))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.poemsCount").value(0));
    }

    @Test
    void createPoemsWithAuthor() throws Exception {
        ResultActions resultActions = createAuthor(new CreateAuthorRequest(AUTHOR_FULL_NAME), token);

        GetAuthorResponse getAuthorResponse = fromJson(resultActions, GetAuthorResponse.class);

        int authorId = getAuthorResponse.getId();

        for (int i = 0; i < 3; i++) {
            createPoem(new CreatePoemRequest(POEM_TITLE, POEM_CONTENT, authorId), token)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.title").value(POEM_TITLE))
                    .andExpect(jsonPath("$.content").value(POEM_CONTENT))
                    .andExpect(jsonPath("$.authorId").value(authorId));
        }

        Thread.sleep(1000);

        getAuthor(authorId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorId))
                .andExpect(jsonPath("$.fullName").value(AUTHOR_FULL_NAME))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.poemsCount").value(3));
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
                .andExpect(jsonPath("$.fullName").value(AUTHOR_FULL_NAME))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.poemsCount").value(0));
    }

    @Test
    void createLongAuthorWithToken() throws Exception {
        createAuthor(new CreateAuthorRequest(VERY_LONG_STRING), token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
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