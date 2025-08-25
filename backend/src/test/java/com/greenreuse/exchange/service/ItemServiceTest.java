package com.greenreuse.exchange.service;

import com.greenreuse.exchange.dto.CreateItemRequest;
import com.greenreuse.exchange.dto.ItemDto;
import com.greenreuse.exchange.model.Item;
import com.greenreuse.exchange.model.User;
import com.greenreuse.exchange.repository.ItemRepository;
import com.greenreuse.exchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceTest {
    @Test
    void createItem_shouldThrowExceptionIfUserNotFound() {
        CreateItemRequest request = new CreateItemRequest();
        request.setTitle("Test Item");
        request.setImages(Collections.singletonList(mock(org.springframework.web.multipart.MultipartFile.class)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class,
                () -> itemService.createItem(request, "notfound@example.com"));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void getItemsByCategory_returnsList() {
        Item item = new Item();
        when(itemRepository.findByCategory(any())).thenReturn(Collections.singletonList(item));
        assertEquals(1, itemService.getItemsByCategory(Item.Category.OTHER).size());
    }

    @Test
    void getItemsByLocation_emptyList() {
        when(itemRepository.findByLocation(any())).thenReturn(Collections.emptyList());
        assertEquals(0, itemService.getItemsByLocation("Unknown").size());
    }

    @Test
    void getItemById_shouldThrowExceptionIfNotFound() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        Exception ex = assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> itemService.getItemById(999L));
        assertTrue(ex.getMessage().contains("Item not found"));
    }

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllItems_returnsList() {
        Item item = new Item();
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        assertEquals(1, itemService.getAllItems().size());
    }

    @Test
    void createItem_success() {
        CreateItemRequest request = new CreateItemRequest();
        request.setTitle("Test Item");
        request.setDescription("Desc");
        request.setLocation("Loc");
        request.setCategory(Item.Category.OTHER);
        request.setType(Item.Type.FREE);
        request.setExchangeFor(null);
        request.setImages(Collections.emptyList());
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> itemService.createItem(request, "test@example.com"));
        assertTrue(ex.getMessage().contains("At least one image is required"));
    }

    @Test
    void getItemsByLocation_returnsList() {
        Item item = new Item();
        when(itemRepository.findByLocation(any())).thenReturn(Collections.singletonList(item));
        assertEquals(1, itemService.getItemsByLocation("Loc").size());
    }
}
