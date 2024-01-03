package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.entity.Author;
import org.nurma.aqyndar.repository.AuthorRepository;
import org.nurma.aqyndar.util.EntityToDTOMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    public static final int MAX_SEARCH_RESULTS = 5;
    private final AuthorRepository authorRepository;
    private final Pageable pageable = Pageable.ofSize(MAX_SEARCH_RESULTS);

    public List<GetAuthorResponse> searchAuthor(final String query) {
        List<Author> authors = authorRepository.findAuthorsByFullNameIsLikeIgnoreCase(query, pageable);
        return authors.stream().map(EntityToDTOMapper::mapAuthorToGetAuthorResponse).toList();
    }
}
