package org.nurma.aqyndar.controller;

import org.junit.jupiter.api.Test;
import org.nurma.aqyndar.configuration.AbstractController;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FaviconControllerTest extends AbstractController {
    @Test
    void faviconDoesNotReturnContent() throws Exception {
        favicon()
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}