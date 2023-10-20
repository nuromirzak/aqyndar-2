package org.nurma.aqyndar.controller;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.PatchAuthorRequest;
import org.nurma.aqyndar.dto.response.DeleteResponse;
import org.nurma.aqyndar.dto.response.GetAuthorResponse;
import org.nurma.aqyndar.service.AuthorService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/author")
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/{id}")
    public GetAuthorResponse getAuthorById(@PathVariable("id") final int id) {
        return authorService.getAuthorById(id);
    }

    @PostMapping
    public GetAuthorResponse createAuthor(@RequestBody final CreateAuthorRequest request) {
        return authorService.createAuthor(request);
    }

    @PatchMapping("/{id}")
    public GetAuthorResponse updateAuthor(@PathVariable("id") final int id,
                                          @RequestBody final PatchAuthorRequest request) {
        return authorService.updateAuthor(id, request);
    }

    @DeleteMapping("/{id}")
    public DeleteResponse deleteAuthor(@PathVariable("id") final int id) {
        return authorService.deleteAuthor(id);
    }
}
