package org.nurma.aqyndar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.RefreshRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.nurma.aqyndar.dto.response.SignupResponse;
import org.nurma.aqyndar.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/who")
    public Authentication getCurrentUser() {
        return authService.getAuthInfo();
    }

    @PostMapping("/signup")
    public SignupResponse signup(@RequestBody @Valid final SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

    @PostMapping("/signin")
    public JwtResponse signin(@RequestBody final SigninRequest signinRequest) {
        return authService.signin(signinRequest);
    }

    @PostMapping("/refresh")
    public JwtResponse getNewAccessToken(@RequestBody final RefreshRequest request) {
        return authService.getAccessToken(request.getRefreshToken());
    }
}
