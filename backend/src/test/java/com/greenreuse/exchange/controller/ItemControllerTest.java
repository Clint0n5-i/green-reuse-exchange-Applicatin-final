package com.greenreuse.exchange.controller;

import com.greenreuse.exchange.dto.ItemDto;
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
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllItems_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/items")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_shouldReturnNotFoundForInvalidId() throws Exception {
        mockMvc.perform(get("/api/items/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemsByCategory_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/items/category/OTHER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByLocation_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/items/location/Unknown")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_shouldReturnUnauthorizedIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/items/create")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
