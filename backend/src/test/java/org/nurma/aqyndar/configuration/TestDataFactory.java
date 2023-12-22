package org.nurma.aqyndar.configuration;

import com.github.javafaker.Faker;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.request.UpdateReactionRequest;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TestDataFactory extends AbstractController {

    private final Faker faker = new Faker();

    public void updateReactionWithRandomUser(UpdateReactionRequest updateReactionRequest) throws Exception {
        SignupRequest signupRequest = createRandomSignupRequest();
        signUp(signupRequest);

        String token = authenticateAndGetToken(signupRequest);
        updateReaction(updateReactionRequest, token);
    }

    private SignupRequest createRandomSignupRequest() {
        return new SignupRequest(
                faker.internet().emailAddress(),
                faker.name().firstName(),
                faker.internet().password()
        );
    }

    private String authenticateAndGetToken(SignupRequest signupRequest) throws Exception {
        SigninRequest signinRequest = new SigninRequest(
                signupRequest.getEmail(),
                signupRequest.getPassword()
        );

        return fromJson(signin(signinRequest), JwtResponse.class).getAccessToken();
    }
}
