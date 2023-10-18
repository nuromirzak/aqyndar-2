package org.nurma.aqyndar.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.nurma.aqyndar.dto.response.SignupResponse;
import org.nurma.aqyndar.entity.Role;
import org.nurma.aqyndar.entity.User;
import org.nurma.aqyndar.exception.AuthenticationException;
import org.nurma.aqyndar.exception.ValidationException;
import org.nurma.aqyndar.security.JwtAuthentication;
import org.nurma.aqyndar.security.JwtProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {
    private static final String INVALID_PASSWORD = "Invalid password";
    private static final String USER_NOT_FOUND = "User with email %s not found";
    private static final String USER_ALREADY_EXISTS = "User with email %s already exists";
    private final UserService userService;
    private final RoleService roleService;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;

    public JwtResponse signin(final SigninRequest authRequest) {
        final User user = userService.getByEmail(authRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND.formatted(authRequest.getEmail())));
        if (user.getPassword().equals(authRequest.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getEmail(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthenticationException(INVALID_PASSWORD);
        }
    }

    public JwtResponse getAccessToken(final String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String email = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.getByEmail(email)
                        .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND.formatted(email)));
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    public JwtAuthentication getAuthInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication) {
            return (JwtAuthentication) authentication;
        }
        return new JwtAuthentication();
    }

    public SignupResponse signup(final SignupRequest signupRequest) {
        if (userService.getByEmail(signupRequest.getEmail()).isPresent()) {
            throw new ValidationException(USER_ALREADY_EXISTS.formatted(signupRequest.getEmail()));
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setPassword(signupRequest.getPassword());

        Set<Role> roles = user.getRoles();
        roles.add(roleService.getDefaultRole());

        userService.save(user);

        SignupResponse signupResponse = new SignupResponse();
        signupResponse.setEmail(user.getEmail());
        return signupResponse;
    }
}
