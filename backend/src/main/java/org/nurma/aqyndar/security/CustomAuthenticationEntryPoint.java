package org.nurma.aqyndar.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nurma.aqyndar.constant.ExceptionTitle;
import org.nurma.aqyndar.dto.response.CustomErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException
    ) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setTitle(ExceptionTitle.AUTHENTICATION);
        customErrorResponse.setDetail(authException.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(customErrorResponse));
    }
}
