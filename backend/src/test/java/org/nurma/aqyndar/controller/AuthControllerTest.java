package org.nurma.aqyndar.controller;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.nurma.aqyndar.configuration.AbstractController;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.RefreshRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.request.UpdateReactionRequest;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthControllerTest extends AbstractController {
    private static final String EMAIL = "steve@gmail.com";
    private static final String FIRST_NAME = "Stevie";
    private static final String PASSWORD = "12345678";
    private static final String VERY_LONG_STRING = "a".repeat(500);

    private static Stream<Arguments> validSignupRequests() {
        return Stream.of(
                Arguments.of(new SignupRequest(EMAIL, FIRST_NAME, PASSWORD))
        );
    }

    @ParameterizedTest
    @MethodSource("validSignupRequests")
    void signup_Success(SignupRequest signupRequest) throws Exception {
        signUp(signupRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(signupRequest.getEmail()))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @ParameterizedTest
    @MethodSource("validSignupRequests")
    void signup_Fail_Duplicate(SignupRequest signupRequest) throws Exception {
        signUp(signupRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(signupRequest.getEmail()))
                .andExpect(jsonPath("$.status").value("success"));

        signUp(signupRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    private static Stream<Arguments> invalidSignupRequests() {
        return Stream.of(
                Arguments.of(new SignupRequest(VERY_LONG_STRING + "@gmail.com", FIRST_NAME, PASSWORD)),
                Arguments.of(new SignupRequest("notemail", FIRST_NAME, PASSWORD)),
                Arguments.of(new SignupRequest(null, FIRST_NAME, PASSWORD)),
                Arguments.of(new SignupRequest(EMAIL, null, PASSWORD)),
                Arguments.of(new SignupRequest(EMAIL, VERY_LONG_STRING, PASSWORD)),
                Arguments.of(new SignupRequest(EMAIL, FIRST_NAME, null)),
                Arguments.of(new SignupRequest(EMAIL, FIRST_NAME, VERY_LONG_STRING))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidSignupRequests")
    void signup_Fail(SignupRequest signupRequest) throws Exception {
        signUp(signupRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.VALIDATION)));
    }

    @Test
    void signin_Success() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, FIRST_NAME, PASSWORD);
        signup_Success(signupRequest);

        signin(new SigninRequest(EMAIL, PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void signin_Fail() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, FIRST_NAME, PASSWORD);
        signup_Success(signupRequest);

        signin(new SigninRequest(EMAIL, "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void signin_Fail_NonExistingAccount() throws Exception {
        signin(new SigninRequest(EMAIL, PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void refresh_success() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, FIRST_NAME, PASSWORD);
        signup_Success(signupRequest);

        JwtResponse jwtResponse = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class
        );

        RefreshRequest refreshRequest = new RefreshRequest(jwtResponse.getRefreshToken());

        refresh(refreshRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isEmpty());
    }

    @Test
    void refresh_Fail() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest("wrongtoken");

        refresh(refreshRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void deleteExistingAccount() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, FIRST_NAME, PASSWORD);
        signup_Success(signupRequest);

        String token = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class
        ).getAccessToken();

        deleteAccount(token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")));
    }

    @Test
    void deleteNonExistingAccount() throws Exception {
        deleteAccount("wrongtoken")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void deleteDeletedAccount() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, FIRST_NAME, PASSWORD);
        signup_Success(signupRequest);

        String token = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class
        ).getAccessToken();

        deleteAccount(token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")));

        deleteAccount(token)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title", is(ExceptionTitle.AUTHENTICATION)));
    }

    @Test
    void deleteAccountWithAuthors() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, FIRST_NAME, PASSWORD);
        signup_Success(signupRequest);

        String token = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class
        ).getAccessToken();

        Faker faker = new Faker();

        for (int i = 0; i < 3; i++) {
            CreateAuthorRequest createAuthorRequest = new CreateAuthorRequest(
                    faker.name().firstName()
            );
            createAuthor(createAuthorRequest, token)
                    .andExpect(status().isOk());
        }

        getAuthors(null, null, null)
                .andExpect(status().isOk());

        deleteAccount(token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")));
    }

    @Test
    void deletingAccountWithReaction() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, FIRST_NAME, PASSWORD);
        signup_Success(signupRequest);

        String token = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class
        ).getAccessToken();

        Faker faker = new Faker();

        CreateAuthorRequest createAuthorRequest = new CreateAuthorRequest(
                faker.name().firstName()
        );
        GetAuthorResponse getAuthorsResponse = fromJson(
                createAuthor(createAuthorRequest, token),
                GetAuthorResponse.class
        );

        CreatePoemRequest createPoemRequest = new CreatePoemRequest(
                faker.book().title(),
                faker.lorem().paragraph(),
                getAuthorsResponse.getId()
        );
        GetPoemResponse getPoemResponse = fromJson(
                createPoem(createPoemRequest, token),
                GetPoemResponse.class
        );

        UpdateReactionRequest updateReactionRequest = new UpdateReactionRequest(
                ReactedEntity.POEM.name(),
                getPoemResponse.getId(),
                1
        );
        updateReaction(updateReactionRequest, token)
                .andExpect(status().isOk());

        deleteAccount(token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")));
    }

    @Test
    void deletingAuthorShouldNotDeleteAccount() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, FIRST_NAME, PASSWORD);
        signup_Success(signupRequest);

        String token = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class
        ).getAccessToken();

        Faker faker = new Faker();

        CreateAuthorRequest createAuthorRequest = new CreateAuthorRequest(
                faker.name().firstName()
        );
        GetAuthorResponse getAuthorsResponse = fromJson(
                createAuthor(createAuthorRequest, token),
                GetAuthorResponse.class
        );

        deleteAuthor(getAuthorsResponse.getId(), token)
                .andExpect(status().isOk());

        getCurrentUser(token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(EMAIL)));
    }
}