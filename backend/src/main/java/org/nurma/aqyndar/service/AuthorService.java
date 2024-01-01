package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.PatchAuthorRequest;
import org.nurma.aqyndar.dto.response.DeleteResponse;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.entity.Author;
import org.nurma.aqyndar.entity.Poem;
import org.nurma.aqyndar.entity.User;
import org.nurma.aqyndar.exception.ResourceNotFound;
import org.nurma.aqyndar.exception.ValidationException;
import org.nurma.aqyndar.repository.AuthorRepository;
import org.nurma.aqyndar.repository.PoemRepository;
import org.nurma.aqyndar.util.EntityToDTOMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private static final String AUTHOR_NOT_FOUND = "Author with id %s not found";
    private static final String AUTHOR_HAVE_POEM = "Author with id %s have poem";
    private static final String AUTHOR_WITH_FULL_NAME_EXISTS = "Author with full name %s already exists";
    private final AuthorRepository authorRepository;
    private final PoemRepository poemRepository;
    private final AuthService authService;

    public GetAuthorResponse getAuthorById(final int id) {
        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            throw new ResourceNotFound(AUTHOR_NOT_FOUND.formatted(id));
        }

        Author author = authorOptional.get();
        int count = getAuthorPoemsCount(id);
        author.setPoemsCount(count);

        return EntityToDTOMapper.mapAuthorToGetAuthorResponse(author);
    }

    public GetAuthorResponse createAuthor(final CreateAuthorRequest request) {
        if (authorRepository.existsByFullName(request.getFullName())) {
            throw new ValidationException(AUTHOR_WITH_FULL_NAME_EXISTS.formatted(request.getFullName()));
        }

        Author author = new Author();
        author.setFullName(request.getFullName());

        User user = authService.getCurrentUserEntity();
        author.setUser(user);

        Author savedAuthor = authorRepository.save(author);
        int count = getAuthorPoemsCount(savedAuthor.getId());
        author.setPoemsCount(count);

        return EntityToDTOMapper.mapAuthorToGetAuthorResponse(savedAuthor);
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
        int count = getAuthorPoemsCount(id);
        author.setPoemsCount(count);

        return EntityToDTOMapper.mapAuthorToGetAuthorResponse(savedAuthor);
    }

    public DeleteResponse deleteAuthor(final int id) {
        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            throw new ResourceNotFound(AUTHOR_NOT_FOUND.formatted(id));
        }

        Poem poem = poemRepository.findByAuthorId(id);

        if (poem != null) {
            throw new ValidationException(AUTHOR_HAVE_POEM.formatted(id));
        }

        authorRepository.deleteById(id);

        return new DeleteResponse();
    }

    public List<GetAuthorResponse> getAllAuthors(final Pageable pageable) {
        Page<Author> authors = authorRepository.findAll(pageable);

        List<GetAuthorResponse> getAuthorResponses = new ArrayList<>();

        for (Author author : authors) {
            int count = getAuthorPoemsCount(author.getId());
            author.setPoemsCount(count);
            GetAuthorResponse getAuthorResponse = EntityToDTOMapper.mapAuthorToGetAuthorResponse(author);
            getAuthorResponses.add(getAuthorResponse);
        }

        return getAuthorResponses;
    }

    private int getAuthorPoemsCount(final int id) {
        return authorRepository.countPoemsByAuthorId(id);
    }
}
