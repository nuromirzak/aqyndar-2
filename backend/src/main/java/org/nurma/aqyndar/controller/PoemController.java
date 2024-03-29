package org.nurma.aqyndar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.PatchPoemRequest;
import org.nurma.aqyndar.dto.response.DeleteResponse;
import org.nurma.aqyndar.dto.response.GetPoemResponse;
import org.nurma.aqyndar.dto.response.GetTopicResponse;
import org.nurma.aqyndar.service.PoemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/poem")
public class PoemController {
    private final PoemService poemService;

    @GetMapping
    public Page<GetPoemResponse> getAllPoems(final Pageable pageable) {
        return poemService.getAllPoems(pageable);
    }

    @GetMapping("/{id}")
    public GetPoemResponse getPoemById(@PathVariable("id") final int id) {
        return poemService.getPoemById(id);
    }

    @GetMapping("/topics")
    public List<GetTopicResponse> getAllTopics() {
        return poemService.getAllTopics();
    }

    @PostMapping
    public GetPoemResponse createPoem(@Valid @RequestBody final CreatePoemRequest request) {
        return poemService.createPoem(request);
    }

    @PatchMapping("/{id}")
    public GetPoemResponse updatePoem(@PathVariable("id") final int id,
                                      @RequestBody final PatchPoemRequest request) {
        return poemService.updatePoem(id, request);
    }

    @DeleteMapping("/{id}")
    public DeleteResponse deletePoem(@PathVariable("id") final int id) {
        return poemService.deletePoem(id);
    }
}
