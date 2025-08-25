package com.greenreuse.exchange.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProfile_shouldReturnUnauthorizedIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getDashboard_shouldReturnUnauthorizedIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/user/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPostedItems_shouldReturnUnauthorizedIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/user/posted-items")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getClaimedItems_shouldReturnUnauthorizedIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/user/claimed-items")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserItems_shouldReturnUnauthorizedIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/user/items")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
