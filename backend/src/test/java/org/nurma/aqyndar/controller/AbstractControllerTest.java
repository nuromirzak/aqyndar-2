package org.nurma.aqyndar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nurma.aqyndar.configuration.IntegrationEnvironment;
import org.nurma.aqyndar.dto.request.CreateAnnotationRequest;
import org.nurma.aqyndar.dto.request.CreateAuthorRequest;
import org.nurma.aqyndar.dto.request.CreatePoemRequest;
import org.nurma.aqyndar.dto.request.PatchAnnotationRequest;
import org.nurma.aqyndar.dto.request.PatchAuthorRequest;
import org.nurma.aqyndar.dto.request.PatchPoemRequest;
import org.nurma.aqyndar.dto.request.RefreshRequest;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.nurma.aqyndar.dto.request.UpdateReactionRequest;
import org.nurma.aqyndar.entity.enums.ReactedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AbstractControllerTest extends IntegrationEnvironment {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private ResultActions performGet(String url) throws Exception {
        return performGetWithToken(url, null);
    }

    private ResultActions performGetWithToken(String url, String token) throws Exception {
        return mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + token))
                .andDo(print());
    }

    private ResultActions performPost(String url, Object request) throws Exception {
        return performPostWithToken(url, request, null);
    }

    private ResultActions performPostWithToken(String url, Object request, String token) throws Exception {
        String contentJson = objectMapper.writeValueAsString(request);
        return mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(contentJson))
                .andDo(print());
    }

    private ResultActions performPatchWithToken(String url, Object request, String token) throws Exception {
        String contentJson = objectMapper.writeValueAsString(request);
        return mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(contentJson))
                .andDo(print());
    }

    private ResultActions performDeleteWithToken(String url, String token) throws Exception {
        return mockMvc.perform(delete(url)
                        .header("Authorization", "Bearer " + token))
                .andDo(print());
    }

    protected <T> T fromJson(ResultActions resultActions, Class<T> clazz) throws Exception {
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, clazz);
    }

    protected ResultActions who(String token) throws Exception {
        return performGetWithToken("/who", token);
    }

    protected ResultActions signUp(SignupRequest signupRequest) throws Exception {
        return performPost("/signup", signupRequest);
    }

    protected ResultActions signin(SigninRequest signinRequest) throws Exception {
        return performPost("/signin", signinRequest);
    }

    protected ResultActions refresh(RefreshRequest refreshRequest) throws Exception {
        return performPost("/refresh", refreshRequest);
    }

    protected ResultActions getAuthor(int id) throws Exception {
        return performGet("/author/" + id);
    }

    protected ResultActions createAuthor(CreateAuthorRequest request, String token) throws Exception {
        return performPostWithToken("/author", request, token);
    }

    protected ResultActions updateAuthor(int id, PatchAuthorRequest request, String token) throws Exception {
        return performPatchWithToken("/author/" + id, request, token);
    }

    protected ResultActions deleteAuthor(int id, String token) throws Exception {
        return performDeleteWithToken("/author/" + id, token);
    }

    protected ResultActions getPoem(int id) throws Exception {
        return performGet("/poem/" + id);
    }

    protected ResultActions createPoem(CreatePoemRequest request, String token) throws Exception {
        return performPostWithToken("/poem", request, token);
    }

    protected ResultActions updatePoem(int id, PatchPoemRequest request, String token) throws Exception {
        return performPatchWithToken("/poem/" + id, request, token);
    }

    protected ResultActions deletePoem(int id, String token) throws Exception {
        return performDeleteWithToken("/poem/" + id, token);
    }

    protected ResultActions getAnnotation(int id) throws Exception {
        return performGet("/annotation/" + id);
    }

    protected ResultActions createAnnotation(CreateAnnotationRequest request, String token) throws Exception {
        return performPostWithToken("/annotation", request, token);
    }

    protected ResultActions updateAnnotation(int id, PatchAnnotationRequest request, String token) throws Exception {
        return performPatchWithToken("/annotation/" + id, request, token);
    }

    protected ResultActions deleteAnnotation(int id, String token) throws Exception {
        return performDeleteWithToken("/annotation/" + id, token);
    }

    protected ResultActions updateReaction(UpdateReactionRequest updateReactionRequest, String token) throws Exception {
        return performPostWithToken("/reaction", updateReactionRequest, token);
    }

    protected ResultActions getReaction(String reactedEntity, int reactedEntityId, String token) throws Exception {
        return performGetWithToken("/reaction?reactedEntity=" + reactedEntity + "&reactedEntityId=" + reactedEntityId, token);
    }
}
