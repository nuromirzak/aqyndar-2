package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.PatchPoemRequest;
import org.nurma.aqyndar.dto.response.DeleteResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.dto.response.GetTopicResponse;
import org.nurma.aqyndar.entity.Author;
import org.nurma.aqyndar.entity.Poem;
import org.nurma.aqyndar.entity.Topic;
import org.nurma.aqyndar.entity.User;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.exception.ValidationException;
import org.nurma.aqyndar.repository.AuthorRepository;
import org.nurma.aqyndar.repository.PoemRepository;
import org.nurma.aqyndar.repository.TopicRepository;
import org.nurma.aqyndar.util.EntityToDTOMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PoemService {
    private static final String AUTHOR_NOT_FOUND = "Author with id %s not found";
    private static final String POEM_NOT_FOUND = "Poem with id %s not found";
    private final PoemRepository poemRepository;
    private final AuthorRepository authorRepository;
    private final AuthService authService;
    private final TopicRepository topicRepository;
    public static final int MIN_GRADE = 1;
    public static final int MAX_GRADE = 12;
    public static final int MIN_COMPLEXITY = 1;
    public static final int MAX_COMPLEXITY = 10;
    private static final String GRADE_OUT_OF_RANGE = "Grade must be between %s and %s";
    private static final String COMPLEXITY_OUT_OF_RANGE = "Complexity must be between %s and %s";

    public GetPoemResponse getPoemById(final int id) {
        Optional<Poem> poemOptional = poemRepository.findById(id);

        if (poemOptional.isEmpty()) {
            throw new ResourceNotFound(POEM_NOT_FOUND.formatted(id));
        }

        Poem poem = poemOptional.get();

        return EntityToDTOMapper.mapPoemToGetPoemResponse(poem);
    }

    public GetPoemResponse createPoem(final CreatePoemRequest request) {
        int authorId = request.getAuthorId();

        Optional<Author> authorOptional = authorRepository.findById(authorId);

        if (authorOptional.isEmpty()) {
            throw new ResourceNotFound(AUTHOR_NOT_FOUND.formatted(authorId));
        }

        Poem poem = new Poem();
        poem.setTitle(request.getTitle());
        poem.setContent(request.getContent());
        poem.setAuthor(authorOptional.get());

        if (request.getSchoolGrade() != null) {
            int schoolGrade = request.getSchoolGrade();

            if (schoolGrade < MIN_GRADE || schoolGrade > MAX_GRADE) {
                throw new ValidationException(GRADE_OUT_OF_RANGE.formatted(MIN_GRADE, MAX_GRADE));
            }

            poem.setSchoolGrade(schoolGrade);
        }

        if (request.getComplexity() != null) {
            int complexity = request.getComplexity();

            if (complexity < MIN_COMPLEXITY || complexity > MAX_COMPLEXITY) {
                throw new ValidationException(COMPLEXITY_OUT_OF_RANGE.formatted(MIN_COMPLEXITY, MAX_COMPLEXITY));
            }

            poem.setComplexity(complexity);
        }

        List<Topic> topics = topicRepository.insertIfNotExist(request.getTopics());
        poem.setTopics(topics);

        User user = authService.getCurrentUserEntity();
        poem.setUser(user);

        Poem savedPoem = poemRepository.save(poem);

        return EntityToDTOMapper.mapPoemToGetPoemResponse(savedPoem);
    }

    public GetPoemResponse updatePoem(final int id, final PatchPoemRequest request) {
        Optional<Poem> poemOptional = poemRepository.findById(id);

        if (poemOptional.isEmpty()) {
            throw new ResourceNotFound(POEM_NOT_FOUND.formatted(id));
        }

        Poem poem = poemOptional.get();

        if (request.getTitle() != null) {
            poem.setTitle(request.getTitle());
        }
        if (request.getAuthorId() != null) {
            int authorId = request.getAuthorId();

            Optional<Author> authorOptional = authorRepository.findById(authorId);

            if (authorOptional.isEmpty()) {
                throw new ResourceNotFound(AUTHOR_NOT_FOUND.formatted(authorId));
            }

            poem.setAuthor(authorOptional.get());
        }
        if (request.getSchoolGrade() != null) {
            int schoolGrade = request.getSchoolGrade();

            if (schoolGrade < MIN_GRADE || schoolGrade > MAX_GRADE) {
                throw new ValidationException(GRADE_OUT_OF_RANGE.formatted(MIN_GRADE, MAX_GRADE));
            }

            poem.setSchoolGrade(schoolGrade);
        }
        if (request.getComplexity() != null) {
            int complexity = request.getComplexity();

            if (complexity < MIN_COMPLEXITY || complexity > MAX_COMPLEXITY) {
                throw new ValidationException(COMPLEXITY_OUT_OF_RANGE.formatted(MIN_COMPLEXITY, MAX_COMPLEXITY));
            }

            poem.setComplexity(complexity);
        }
        if (request.getTopics() != null) {
            List<Topic> topics = topicRepository.insertIfNotExist(request.getTopics());
            for (Topic topic : topics) {
                if (!poem.getTopics().contains(topic)) {
                    poem.getTopics().add(topic);
                }
            }
        }
        Poem savedPoem = poemRepository.save(poem);

        return EntityToDTOMapper.mapPoemToGetPoemResponse(savedPoem);
    }

    public DeleteResponse deletePoem(final int id) {
        Optional<Poem> poemOptional = poemRepository.findById(id);

        if (poemOptional.isEmpty()) {
            throw new ResourceNotFound(POEM_NOT_FOUND.formatted(id));
        }

        poemRepository.deleteById(id);

        return new DeleteResponse();
    }

    public List<GetPoemResponse> getAllPoems(final Pageable pageable) {
        Page<Poem> poems = poemRepository.findAll(pageable);

        List<GetPoemResponse> getPoemResponses = new ArrayList<>();

        for (Poem poem : poems) {
            GetPoemResponse getPoemResponse = EntityToDTOMapper.mapPoemToGetPoemResponse(poem);
            getPoemResponses.add(getPoemResponse);
        }

        return getPoemResponses;
    }

    public List<GetTopicResponse> getAllTopics() {
        List<Topic> topics = topicRepository.findAll();

        return topics.stream()
                .map(EntityToDTOMapper::mapTopicToGetTopicResponse)
                .toList();
    }
}
