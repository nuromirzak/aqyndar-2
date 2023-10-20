package org.nurma.aqyndar.controller;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.CreateAnnotationRequest;
import org.nurma.aqyndar.dto.request.PatchAnnotationRequest;
import org.nurma.aqyndar.dto.response.DeleteResponse;
import org.nurma.aqyndar.dto.response.GetAnnotationResponse;
import org.nurma.aqyndar.service.AnnotationService;
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
@RequestMapping("/annotation")
public class AnnotationController {
    private final AnnotationService annotationService;

    @GetMapping("/{id}")
    public GetAnnotationResponse getAnnotationById(@PathVariable("id") final int id) {
        return annotationService.getAnnotationById(id);
    }

    @PostMapping
    public GetAnnotationResponse createAnnotation(@RequestBody final CreateAnnotationRequest request) {
        return annotationService.createAnnotation(request);
    }

    @PatchMapping("/{id}")
    public GetAnnotationResponse updateAnnotation(@PathVariable("id") final int id,
                                                  @RequestBody final PatchAnnotationRequest request) {
        return annotationService.updateAnnotation(id, request);

    }

    @DeleteMapping("/{id}")
    public DeleteResponse deleteAnnotation(@PathVariable("id") final int id) {
        return annotationService.deleteAnnotation(id);
    }
}
