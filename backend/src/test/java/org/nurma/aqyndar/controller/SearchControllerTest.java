package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.nurma.aqyndar.configuration.TestDataFactory;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.service.SearchService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@SuppressWarnings("SpellCheckingInspection")
class SearchControllerTest extends TestDataFactory {
    private String token;
    private static final List<CreateAuthorRequest> CYRILLIC_AUTHOR_NAMES = List.of(
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
    private static final List<CreateAuthorRequest> LATIN_AUTHOR_NAMES = List.of(
            new CreateAuthorRequest("William Shakespeare"),
            new CreateAuthorRequest("Geoffrey Chaucer"),
            new CreateAuthorRequest("John Milton"),
            new CreateAuthorRequest("William Wordsworth"),
            new CreateAuthorRequest("Samuel Taylor Coleridge"),
            new CreateAuthorRequest("Lord Byron"),
            new CreateAuthorRequest("Percy Bysshe Shelley"),
            new CreateAuthorRequest("John Keats"),
            new CreateAuthorRequest("Robert Browning"),
            new CreateAuthorRequest("Elizabeth Barrett Browning")
    );


    @BeforeEach
    void setUp() throws Exception {
        SignupRequest signupRequest = super.createRandomSignupRequest();
        signUp(signupRequest);
        token = super.authenticateAndGetToken(signupRequest);
    }

    private void addAllAuthors(List<CreateAuthorRequest> createAuthorRequests) throws Exception {
        for (CreateAuthorRequest createAuthorRequest : createAuthorRequests) {
            super.createAuthor(createAuthorRequest, token);
        }
    }

    private static Stream<Arguments> provideSearchScenarios() {
        return Stream.of(
                Arguments.of("ба", List.of("Абай Құнанбайұлы", "Жамбыл Жабайұлы", "Мағжан Жұмабаев"),
                        CYRILLIC_AUTHOR_NAMES),
                Arguments.of("el", List.of("Samuel Taylor Coleridge", "Percy Bysshe Shelley"),
                        LATIN_AUTHOR_NAMES),
                Arguments.of("ба", List.of("Абай Құнанбайұлы", "Жамбыл Жабайұлы", "Мағжан Жұмабаев", "Бауыржан Момышулы"),
                        CYRILLIC_AUTHOR_NAMES),
                Arguments.of("EL", List.of("Samuel Taylor Coleridge", "Percy Bysshe Shelley", "Elizabeth Barrett Browning"),
                        LATIN_AUTHOR_NAMES)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSearchScenarios")
    void searchTest(String searchString, List<String> expectedNames, List<CreateAuthorRequest> authors)
            throws Exception {
        addAllAuthors(authors);
        ResultActions resultActions = searchAuthors(searchString);
        resultActions.andExpect(status().isOk());

        List<GetAuthorResponse> actualAuthors = fromJsonArray(resultActions, GetAuthorResponse.class);

        assertContainsAllNames(expectedNames, actualAuthors);
    }

    private void assertContainsAllNames(List<String> expectedNames, List<GetAuthorResponse> actualAuthors) {
        expectedNames.forEach(expectedName -> {
            boolean found = actualAuthors.stream()
                    .anyMatch(actualAuthor -> actualAuthor.getFullName().equals(expectedName));
            Assertions.assertTrue(found);
        });
    }

    @Test
    void limitWorks() throws Exception {
        addAllAuthors(CYRILLIC_AUTHOR_NAMES);

        searchAuthors("")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(SearchService.MAX_SEARCH_RESULTS));
    }

    @Test
    void withNoAuthors() throws Exception {
        searchAuthors("ба")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}