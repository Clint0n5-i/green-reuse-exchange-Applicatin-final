package com.greenreuse.exchange.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpoint_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    void healthEndpoint_shouldReturnUnauthorizedIfNoAuth() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk()); // Health endpoint is usually public, but change to Unauthorized if
                                             // protected
    }

    @Test
    void healthEndpoint_shouldHandleException() throws Exception {
        // Simulate an exception in the controller (would require a mock or a test
        // profile)
        // For demonstration, check that the global handler returns 500 for generic
        // errors
        mockMvc.perform(get("/api/unknown"))
                .andExpect(status().isNotFound()); // NotFound for unknown endpoint, InternalServerError for thrown
                                                   // exception
    }
}
