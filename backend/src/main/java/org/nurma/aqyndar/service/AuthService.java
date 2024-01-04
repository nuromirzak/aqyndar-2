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
import org.nurma.aqyndar.exception.CustomAuthenticationException;
import org.nurma.aqyndar.exception.ValidationException;
import org.nurma.aqyndar.repository.AnnotationRepository;
import org.nurma.aqyndar.repository.AuthorRepository;
import org.nurma.aqyndar.repository.PoemRepository;
import org.nurma.aqyndar.repository.ReactionRepository;
import org.nurma.aqyndar.repository.UserRepository;
import org.nurma.aqyndar.security.JwtAuthentication;
import org.nurma.aqyndar.security.JwtProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {
    private static final String INVALID_PASSWORD = "Invalid password";
    private static final String USER_NOT_FOUND = "User with email %s not found";
    private static final String USER_ALREADY_EXISTS = "User with email %s already exists";
    private static final String AUTHENTICATION_TYPE_NOT_SUPPORTED = "Authentication type %s not supported";
    private final UserRepository userRepository;
    private final ReactionRepository reactionRepository;
    private final PoemRepository poemRepository;
    private final AuthorRepository authorRepository;
    private final AnnotationRepository annotationRepository;
    private final RoleService roleService;
    private final JwtProvider jwtProvider;

    public JwtResponse signin(final SigninRequest authRequest) {
        final User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new CustomAuthenticationException(USER_NOT_FOUND.formatted(authRequest.getEmail())));
        if (user.getPassword().equals(authRequest.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new CustomAuthenticationException(INVALID_PASSWORD);
        }
    }

    public JwtResponse getAccessToken(final String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String email = claims.getSubject();
            final User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomAuthenticationException(USER_NOT_FOUND.formatted(email)));
            final String accessToken = jwtProvider.generateAccessToken(user);
            return new JwtResponse(accessToken, null);
        }
        throw new CustomAuthenticationException("Invalid refresh token");
    }

    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;

        String email = jwtAuthentication.getEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomAuthenticationException(USER_NOT_FOUND.formatted(email)));
    }

    public SignupResponse signup(final SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new ValidationException(USER_ALREADY_EXISTS.formatted(signupRequest.getEmail()));
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setPassword(signupRequest.getPassword());

        Set<Role> roles = user.getRoles();
        roles.add(roleService.getDefaultRole());

        userRepository.save(user);

        SignupResponse signupResponse = new SignupResponse();
        signupResponse.setEmail(user.getEmail());
        return signupResponse;
    }

    @Transactional
    public void deleteAccount() {
        final User user = getCurrentUserEntity();

        reactionRepository.deleteByUserId(user.getId());
        annotationRepository.deleteByUserId(user.getId());
        poemRepository.deleteByUserId(user.getId());
        authorRepository.deleteByUserId(user.getId());

        userRepository.delete(user);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new CustomAuthenticationException(USER_NOT_FOUND.formatted(user.getEmail()));
        }
    }
}
