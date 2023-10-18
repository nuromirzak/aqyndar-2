package org.nurma.aqyndar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nurma.aqyndar.configuration.IntegrationEnvironment;
import org.nurma.aqyndar.dto.request.SigninRequest;
import org.nurma.aqyndar.dto.request.RefreshRequest;
import org.nurma.aqyndar.dto.request.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    protected <T> T fromJson(MvcResult result, Class<T> clazz) throws Exception {
        String contentAsString = result.getResponse().getContentAsString();
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
}
