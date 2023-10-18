package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.nurma.aqyndar.dto.request.RefreshRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthControllerTest extends AbstractControllerTest {
    private static final String EMAIL = "steve@gmail.com";
    private static final String FIRST_NAME = "Stevie";
    private static final String PASSWORD = "12345678";

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

    private static Stream<Arguments> invalidSignupRequests() {
        return Stream.of(
                Arguments.of(new SignupRequest("notemail", FIRST_NAME, PASSWORD)),
                Arguments.of(new SignupRequest("", FIRST_NAME, PASSWORD)),
                Arguments.of(new SignupRequest(null, FIRST_NAME, PASSWORD)),
                Arguments.of(new SignupRequest(EMAIL, "", PASSWORD)),
                Arguments.of(new SignupRequest(EMAIL, null, PASSWORD)),
                Arguments.of(new SignupRequest(EMAIL, FIRST_NAME, "")),
                Arguments.of(new SignupRequest(EMAIL, FIRST_NAME, null))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidSignupRequests")
    void signup_Fail(SignupRequest signupRequest) throws Exception {
        signUp(signupRequest)
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_success() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, PASSWORD, PASSWORD);
        signup_Success(signupRequest);

        JwtResponse jwtResponse = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)).andReturn(),
                JwtResponse.class
        );

        System.out.printf("jwtResponse=%s\n", jwtResponse);

        RefreshRequest refreshRequest = new RefreshRequest(jwtResponse.getRefreshToken());

        System.out.printf("refreshRequest=%s\n", refreshRequest);

        refresh(refreshRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isEmpty());
    }

    @Test
    void refresh_Fail() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest("wrongtoken");

        refresh(refreshRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void who_with_anonymous() throws Exception {
        who(null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.email").isEmpty());
    }

    @Test
    void who_with_user() throws Exception {
        SignupRequest signupRequest = new SignupRequest(EMAIL, PASSWORD, PASSWORD);
        signUp(signupRequest);

        JwtResponse jwtResponse = fromJson(
                signin(new SigninRequest(EMAIL, PASSWORD)).andReturn(),
                JwtResponse.class
        );

        who(jwtResponse.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL));
    }
}