package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.request.RefreshRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthControllerTest extends AbstractControllerTest {
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
        SignupRequest signupRequest = new SignupRequest(EMAIL, PASSWORD, PASSWORD);
        signup_Success(signupRequest);

        signin(new SigninRequest(EMAIL, PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void signin_Fail() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, PASSWORD, PASSWORD);
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
        SignupRequest signupRequest = new SignupRequest(EMAIL, PASSWORD, PASSWORD);
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
    void who_with_anonymous() throws Exception {
        who(null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void who_with_user() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, PASSWORD, PASSWORD);
        signUp(signupRequest);

        JwtResponse jwtResponse = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)),
                JwtResponse.class
        );

        who(jwtResponse.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL));
    }
}