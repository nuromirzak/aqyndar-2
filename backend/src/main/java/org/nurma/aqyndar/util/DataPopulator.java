package org.nurma.aqyndar.util;

import com.github.javafaker.Faker;
import org.nurma.aqyndar.dto.request.CreateAnnotationRequest;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.request.UpdateReactionRequest;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.dto.response.GetWhoResponse;
import org.nurma.aqyndar.dto.response.JwtResponse;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.nurma.aqyndar.entity.enums.ReactionType;
import org.nurma.aqyndar.service.PoemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class DataPopulator {
    private final Faker faker;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(DataPopulator.class);
    private final List<SignupRequest> signupRequests = List.of(
            new SignupRequest("rosaline.vonrueden@yahoo.com", "Rosaline", "x4d7ykluyr"),
            new SignupRequest("yen.hackett@hotmail.com", "Yen", "q5ohuwoe"),
            new SignupRequest("jacquelyne.gibson@gmail.com", "Jacquelyne", "y2onxtdf6hv7"),
            new SignupRequest("deshawn.mccullough@yahoo.com", "Deshawn", "qq86qrkmggsyz"),
            new SignupRequest("leonora.mante@yahoo.com", "Leonora", "kpm22mwo37cfcg"),
            new SignupRequest("sheldon.turner@hotmail.com", "Sheldon", "6jmasl3shebqvuc"),
            new SignupRequest("blanch.pfannerstill@hotmail.com", "Blanch", "df6ehwwe0"),
            new SignupRequest("vanda.wolff@yahoo.com", "Vanda", "kfvtqegu7fyru"),
            new SignupRequest("luke.prosacco@hotmail.com", "Luke", "vf8oyo31eklp78"),
            new SignupRequest("pablo.lindgren@gmail.com", "Pablo", "h5vwp7002")
    );
    private final List<CreateAuthorRequest> createAuthorRequests = List.of(
            new CreateAuthorRequest("Ілияс Жансүгіров"),
            new CreateAuthorRequest("Абай Құнанбайұлы"),
            new CreateAuthorRequest("Мұхтар Әуезов"),
            new CreateAuthorRequest("Сәбит Мұқанов"),
            new CreateAuthorRequest("Ғабит Мүсірепов"),
            new CreateAuthorRequest("Жамбыл Жабайұлы"),
            new CreateAuthorRequest("Мағжан Жұмабаев"),
            new CreateAuthorRequest("Жүсіпбек Аймауытов"),
            new CreateAuthorRequest("Ильяс Есенберлин"),
            new CreateAuthorRequest("Сәкен Сейфуллин"),
            new CreateAuthorRequest("Олжас Сүлейменов"),
            new CreateAuthorRequest("Бауыржан Момышулы"),
            new CreateAuthorRequest("Шерхан Муртаза"),
            new CreateAuthorRequest("Дулат Исабеков"),
            new CreateAuthorRequest("Мұқағали Мақатаев")
    );
    private final List<GetAuthorResponse> getAuthorResponses = new ArrayList<>();
    private final List<GetPoemResponse> getPoemResponses = new ArrayList<>();
    private final List<UserInfo> userInfos = new ArrayList<>();
    private final List<String> topics;

    {
        topics = new ArrayList<>();
        topics.add("Nature");
        topics.add("Love");
        topics.add("War");
        topics.add("Fantasy");
        topics.add("Tragedy");
        topics.add("Comedy");
        topics.add("Adventure");
        topics.add("Mystery");
        topics.add("Historical");
        topics.add("Inspirational");
    }

    private final List<GetAnnotationResponse> getAnnotationResponses = new ArrayList<>();

    public DataPopulator(final String baseUrl) {
        this.restTemplate = new RestTemplateBuilder()
                .rootUri(baseUrl)
                .build();
        this.faker = new Faker();
    }

    public static void main(final String[] args) {
        final String baseUrl = "http://localhost:8080";
        final DataPopulator dataPopulator = new DataPopulator(baseUrl);
        dataPopulator.populate();
    }

    public void populate() {
        long startTime = System.currentTimeMillis();

        final int poemsCount = 250;
        final int annotationsCount = 250;
        final int reactionsCount = 2000;

        signupRequests.forEach(this::registerUser);
        signupRequests.forEach(this::signinUser);
        createAuthorRequests.forEach(this::addAuthor);
        addPoems(poemsCount);
        addAnnotations(annotationsCount);
        addReactions(reactionsCount);

        logResponses();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Populated in {} ms", duration);
    }

    private void registerUser(final SignupRequest signupRequest) {
        restTemplate.postForObject("/signup", signupRequest, Void.class);
    }

    private void signinUser(final SignupRequest signupRequest) {
        JwtResponse jwtResponse = restTemplate.postForObject("/signin",
                new SigninRequest(signupRequest.getEmail(), signupRequest.getPassword()), JwtResponse.class);
        userInfos.add(new UserInfo(jwtResponse, getCurrentUser(jwtResponse.getAccessToken())));
    }

    private void addAuthor(final CreateAuthorRequest authorRequest) {
        UserInfo randomUserInfo = getRandomElement(userInfos);
        HttpHeaders headers = createHeadersWithToken(randomUserInfo.jwtResponse().getAccessToken());
        ResponseEntity<GetAuthorResponse> response = restTemplate.exchange(
                "/author", HttpMethod.POST, new HttpEntity<>(authorRequest, headers), GetAuthorResponse.class);
        getAuthorResponses.add(response.getBody());
    }

    private void addPoems(final int poemsCount) {
        for (int i = 0; i < poemsCount; i++) {
            CreatePoemRequest createPoemRequest = generatePoemRequest();
            UserInfo randomUserInfo = getRandomElement(userInfos);
            HttpHeaders headers = createHeadersWithToken(randomUserInfo.jwtResponse().getAccessToken());
            getPoemResponses.add(restTemplate.exchange(
                            "/poem", HttpMethod.POST,
                            new HttpEntity<>(createPoemRequest, headers), GetPoemResponse.class)
                    .getBody());
        }
    }

    private CreatePoemRequest generatePoemRequest() {
        String title = faker.book().title();
        List<String> paragraphs = faker.lorem().paragraphs(faker.number().randomDigit() + 1);
        String content = String.join("\n", paragraphs);
        GetAuthorResponse getAuthorResponse = getRandomElement(getAuthorResponses);
        Integer schoolGrade = faker.bool().bool()
                ? faker.number().numberBetween(PoemService.MIN_GRADE, PoemService.MAX_GRADE) : null;
        Integer complexity = faker.bool().bool()
                ? faker.number().numberBetween(PoemService.MIN_COMPLEXITY, PoemService.MAX_COMPLEXITY) : null;

        return new CreatePoemRequest(
                title, content, getAuthorResponse.getId(),
                schoolGrade, complexity, getRandomTopics()
        );
    }

    private void addAnnotations(final int annotationsCount) {
        for (int i = 0; i < annotationsCount; i++) {
            CreateAnnotationRequest createAnnotationRequest = generateAnnotationRequest();
            UserInfo randomUserInfo = getRandomElement(userInfos);
            HttpHeaders headers = createHeadersWithToken(randomUserInfo.jwtResponse().getAccessToken());
            getAnnotationResponses.add(restTemplate.exchange(
                            "/annotation", HttpMethod.POST, new HttpEntity<>(createAnnotationRequest, headers),
                            GetAnnotationResponse.class)
                    .getBody());
        }
    }

    private CreateAnnotationRequest generateAnnotationRequest() {
        GetPoemResponse randomGetPoemResponse = getRandomElement(getPoemResponses);
        int startRangeIndex = faker.number().numberBetween(0, randomGetPoemResponse.getContent().length());
        int endRangeIndex = faker.number().numberBetween(startRangeIndex, randomGetPoemResponse.getContent().length());

        return new CreateAnnotationRequest(
                faker.lorem().paragraph(), startRangeIndex, endRangeIndex, randomGetPoemResponse.getId()
        );
    }

    private void addReactions(final int reactionsCount) {
        for (int i = 0; i < reactionsCount; i++) {
            UpdateReactionRequest updateReactionRequest = generateReactionRequest();
            UserInfo randomUserInfo = getRandomElement(userInfos);
            restTemplate.exchange("/reaction", HttpMethod.POST,
                    new HttpEntity<>(updateReactionRequest,
                            createHeadersWithToken(randomUserInfo.jwtResponse().getAccessToken())), Void.class);
        }
    }

    private UpdateReactionRequest generateReactionRequest() {
        final int maxProbability = 100;
        final int likeProbability = 80;
        UpdateReactionRequest updateReactionRequest = new UpdateReactionRequest();
        int reactionProbability = faker.number().numberBetween(0, maxProbability);
        ReactionType reactionType = reactionProbability < likeProbability ? ReactionType.LIKE : ReactionType.DISLIKE;
        updateReactionRequest.setReactionType(reactionType.getValue());

        if (faker.bool().bool()) {
            GetPoemResponse getPoemResponse = getRandomElement(getPoemResponses);
            updateReactionRequest.setReactedEntity(ReactedEntity.POEM.name());
            updateReactionRequest.setReactedEntityId(getPoemResponse.getId());
        } else {
            GetAnnotationResponse getAnnotationResponse = getRandomElement(getAnnotationResponses);
            updateReactionRequest.setReactedEntity(ReactedEntity.ANNOTATION.name());
            updateReactionRequest.setReactedEntityId(getAnnotationResponse.getId());
        }

        return updateReactionRequest;
    }

    private void logResponses() {
        logger.info("{}", userInfos);
        logger.info("{}", getAuthorResponses);
        logger.info("{}", getPoemResponses);
        logger.info("{}", getAnnotationResponses);
    }

    public List<String> getRandomTopics() {
        if (faker.bool().bool()) {
            return Collections.emptyList();
        }
        int topicsCount = faker.number().numberBetween(1, topics.size());
        Collections.shuffle(topics);
        return topics.subList(0, topicsCount);
    }

    private GetWhoResponse getCurrentUser(final String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<GetWhoResponse>
                response = restTemplate.exchange("/profile", HttpMethod.GET, entity, GetWhoResponse.class);
        return response.getBody();
    }

    private <T> T getRandomElement(final List<T> list) {
        return list.get(faker.number().numberBetween(0, list.size()));
    }

    private HttpHeaders createHeadersWithToken(final String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private record UserInfo(JwtResponse jwtResponse, GetWhoResponse getWhoResponse) {
    }
}
