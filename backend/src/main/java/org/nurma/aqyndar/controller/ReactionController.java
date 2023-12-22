package org.nurma.aqyndar.controller;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.request.UpdateReactionRequest;
import org.nurma.aqyndar.dto.response.GetReactionResponse;
import org.nurma.aqyndar.dto.response.TopEntityResponse;
import org.nurma.aqyndar.dto.response.UpdateReactionResponse;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.nurma.aqyndar.entity.enums.TopEntity;
import org.nurma.aqyndar.service.ReactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reaction")
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping
    public UpdateReactionResponse updateReaction(@RequestBody final UpdateReactionRequest updateReactionRequest) {
        return reactionService.updateReaction(updateReactionRequest);
    }

    @GetMapping
    public GetReactionResponse getReaction(@RequestParam final ReactedEntity reactedEntity,
                                           @RequestParam final int reactedEntityId) {
        return reactionService.getReaction(reactedEntity, reactedEntityId);
    }

    @GetMapping("/top")
    public List<TopEntityResponse> getTopEntities(@RequestParam("topEntity") final TopEntity topEntity) {
        return reactionService.getTopEntities(topEntity);
    }
}
