package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.PatchPoemRequest;
import org.nurma.aqyndar.dto.response.DeletePoemResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.entity.Author;
import org.nurma.aqyndar.entity.Poem;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.repository.AuthorRepository;
import org.nurma.aqyndar.repository.PoemRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PoemService {
    private static final String AUTHOR_NOT_FOUND = "Author with id %s not found";
    private static final String POEM_NOT_FOUND = "Poem with id %s not found";
    private final PoemRepository poemRepository;
    private final AuthorRepository authorRepository;

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
        return getPoemResponse;
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

    public DeletePoemResponse deletePoem(final int id) {
        Optional<Poem> poemOptional = poemRepository.findById(id);

        if (poemOptional.isEmpty()) {
            throw new ResourceNotFound(POEM_NOT_FOUND.formatted(id));
        }

        poemRepository.deleteById(id);

        return new DeletePoemResponse();
    }
}
