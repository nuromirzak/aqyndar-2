package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.PatchPoemRequest;
import org.nurma.aqyndar.dto.response.DeleteResponse;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.entity.Annotation;
import org.nurma.aqyndar.entity.Author;
import org.nurma.aqyndar.entity.Poem;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.exception.ValidationException;
import org.nurma.aqyndar.repository.AnnotationRepository;
import org.nurma.aqyndar.repository.AuthorRepository;
import org.nurma.aqyndar.repository.PoemRepository;
import org.nurma.aqyndar.util.AnnotationUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PoemService {
    private static final String AUTHOR_NOT_FOUND = "Author with id %s not found";
    private static final String POEM_NOT_FOUND = "Poem with id %s not found";
    private final PoemRepository poemRepository;
    private final AuthorRepository authorRepository;
    private final AnnotationRepository annotationRepository;

    public GetPoemResponse getPoemById(final int id) {
        Optional<Poem> poemOptional = poemRepository.findById(id);

        if (poemOptional.isEmpty()) {
            throw new ResourceNotFound(POEM_NOT_FOUND.formatted(id));
        }

        Poem poem = poemOptional.get();
        GetPoemResponse getPoemResponse = new GetPoemResponse();
        getPoemResponse.setId(poem.getId());
        getPoemResponse.setTitle(poem.getTitle());
        getPoemResponse.setContent(poem.getContent());
        getPoemResponse.setAuthorId(poem.getAuthor().getId());

        List<Integer> parsedAnnotations = AnnotationUtil.extractAnnotations(poem.getContent());
        List<Annotation> annotations = annotationRepository.findAllById(parsedAnnotations);

        Map<Integer, GetAnnotationResponse> annotationMap = new HashMap<>();
        for (Annotation annotation : annotations) {
            annotationMap.put(annotation.getId(),
                    new GetAnnotationResponse(annotation.getId(), annotation.getContent()));
        }
        getPoemResponse.setAnnotations(annotationMap);

        return getPoemResponse;
    }

    public GetPoemResponse createPoem(final CreatePoemRequest request) {
        if (AnnotationUtil.hasOverlappingAnnotations(request.getContent())) {
            throw new ValidationException("Overlapping annotations are not supported");
        }

        int authorId = request.getAuthorId();

        Optional<Author> authorOptional = authorRepository.findById(authorId);

        if (authorOptional.isEmpty()) {
            throw new ResourceNotFound(AUTHOR_NOT_FOUND.formatted(authorId));
        }

        Poem poem = new Poem();
        poem.setTitle(request.getTitle());
        poem.setContent(request.getContent());
        poem.setAuthor(authorOptional.get());
        Poem savedPoem = poemRepository.save(poem);

        GetPoemResponse getPoemResponse = new GetPoemResponse();
        getPoemResponse.setId(savedPoem.getId());
        getPoemResponse.setTitle(savedPoem.getTitle());
        getPoemResponse.setContent(savedPoem.getContent());
        getPoemResponse.setAuthorId(savedPoem.getAuthor().getId());
        return getPoemResponse;
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
        if (request.getContent() != null) {
            if (AnnotationUtil.hasOverlappingAnnotations(request.getContent())) {
                throw new ValidationException("Overlapping annotations are not supported");
            }
            poem.setContent(request.getContent());
        }
        if (request.getAuthorId() != null) {
            int authorId = request.getAuthorId();

            Optional<Author> authorOptional = authorRepository.findById(authorId);

            if (authorOptional.isEmpty()) {
                throw new ResourceNotFound(AUTHOR_NOT_FOUND.formatted(authorId));
            }

            poem.setAuthor(authorOptional.get());
        }
        Poem savedPoem = poemRepository.save(poem);

        GetPoemResponse getPoemResponse = new GetPoemResponse();
        getPoemResponse.setId(savedPoem.getId());
        getPoemResponse.setTitle(savedPoem.getTitle());
        getPoemResponse.setContent(savedPoem.getContent());
        getPoemResponse.setAuthorId(savedPoem.getAuthor().getId());
        return getPoemResponse;
    }

    public DeleteResponse deletePoem(final int id) {
        Optional<Poem> poemOptional = poemRepository.findById(id);

        if (poemOptional.isEmpty()) {
            throw new ResourceNotFound(POEM_NOT_FOUND.formatted(id));
        }

        poemRepository.deleteById(id);

        return new DeleteResponse();
    }
}
