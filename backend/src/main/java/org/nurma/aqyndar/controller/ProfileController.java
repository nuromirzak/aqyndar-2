package org.nurma.aqyndar.controller;


import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.dto.response.GetLikesResponse;
import org.nurma.aqyndar.dto.response.GetWhoResponse;
import org.nurma.aqyndar.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public GetWhoResponse getCurrentUser() {
        return profileService.getCurrentUser();
    }

    @GetMapping("/{id}")
    public GetWhoResponse getUserById(@PathVariable("id") final int id) {
        return profileService.getUser(id);
    }

    @GetMapping("/{id}/likes")
    public GetLikesResponse getLikes(@PathVariable("id") final int id) {
        return profileService.getLikes(id);
    }
}
