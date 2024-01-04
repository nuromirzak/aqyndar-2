package org.nurma.aqyndar.configuration;

import com.github.javafaker.Faker;
import org.nurma.aqyndar.dto.request.CreateAnnotationRequest;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.request.UpdateReactionRequest;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.dto.response.GetReactionResponse;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.nurma.aqyndar.dto.response.UpdateReactionResponse;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TestDataFactory extends AbstractController {

    private final Faker faker = new Faker();

    protected UpdateReactionResponse updateReactionWithRandomUser(UpdateReactionRequest updateReactionRequest) throws Exception {
        SignupRequest signupRequest = createRandomSignupRequest();
        signUp(signupRequest);

        String token = authenticateAndGetToken(signupRequest);

        return fromJson(
                updateReaction(updateReactionRequest, token),
                UpdateReactionResponse.class
        );
    }

    protected SignupRequest createRandomSignupRequest() {
        return new SignupRequest(
                faker.internet().emailAddress(),
                faker.name().firstName(),
                faker.internet().password()
        );
    }

    protected String authenticateAndGetToken(SignupRequest signupRequest) throws Exception {
        SigninRequest signinRequest = new SigninRequest(
                signupRequest.getEmail(),
                signupRequest.getPassword()
        );

        return fromJson(signin(signinRequest), JwtResponse.class).getAccessToken();
    }

    protected GetAnnotationResponse createRandomAnnotationWithPoemId(int poemId) throws Exception {
        GetPoemResponse poem = fromJson(
                getPoem(poemId),
                GetPoemResponse.class
        );

        SignupRequest signupRequest = createRandomSignupRequest();
        signUp(signupRequest);

        String token = authenticateAndGetToken(signupRequest);

        CreateAnnotationRequest createAnnotationRequest = new CreateAnnotationRequest();
        createAnnotationRequest.setContent(faker.lorem().paragraph());
        createAnnotationRequest.setStartRangeIndex(0);
        createAnnotationRequest.setEndRangeIndex(faker.number().numberBetween(1, poem.getContent().length()));
        createAnnotationRequest.setPoemId(poemId);

        return fromJson(
                createAnnotation(createAnnotationRequest, token),
                GetAnnotationResponse.class
        );
    }
}
