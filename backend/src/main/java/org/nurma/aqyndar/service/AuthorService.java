package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.PatchAuthorRequest;
import org.nurma.aqyndar.dto.response.DeleteAuthorResponse;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.entity.Author;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private static final String AUTHOR_NOT_FOUND = "Author with id %s not found";
    private final AuthorRepository authorRepository;

    public GetAuthorResponse getAuthorById(final int id) {
        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            throw new ResourceNotFound(AUTHOR_NOT_FOUND.formatted(id));
        }

        Author author = authorOptional.get();
        GetAuthorResponse getAuthorResponse = new GetAuthorResponse();
        getAuthorResponse.setId(author.getId());
        getAuthorResponse.setFullName(author.getFullName());
        return getAuthorResponse;
    }

    public GetAuthorResponse createAuthor(final CreateAuthorRequest request) {
        Author author = new Author();
        author.setFullName(request.getFullName());

        Author savedAuthor = authorRepository.save(author);

        GetAuthorResponse getAuthorResponse = new GetAuthorResponse();
        getAuthorResponse.setId(savedAuthor.getId());
        getAuthorResponse.setFullName(savedAuthor.getFullName());
        return getAuthorResponse;
    }


    public GetAuthorResponse updateAuthor(final int id, final PatchAuthorRequest request) {
        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            throw new ResourceNotFound(AUTHOR_NOT_FOUND.formatted(id));
        }

        Author author = authorOptional.get();

        if (request.getFullName() != null) {
            author.setFullName(request.getFullName());
        }

        Author savedAuthor = authorRepository.save(author);

        GetAuthorResponse getAuthorResponse = new GetAuthorResponse();
        getAuthorResponse.setId(savedAuthor.getId());
        getAuthorResponse.setFullName(savedAuthor.getFullName());
        return getAuthorResponse;
    }

    public DeleteAuthorResponse deleteAuthor(final int id) {
        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            throw new ResourceNotFound(AUTHOR_NOT_FOUND.formatted(id));
        }

        Author author = authorOptional.get();

        authorRepository.delete(author);

        return new DeleteAuthorResponse();
    }
}
