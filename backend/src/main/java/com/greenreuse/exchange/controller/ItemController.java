package com.greenreuse.exchange.controller;

import com.greenreuse.exchange.dto.CreateItemRequest;
import com.greenreuse.exchange.dto.ItemDto;
import com.greenreuse.exchange.model.Item;
import com.greenreuse.exchange.model.User;
import com.greenreuse.exchange.service.ItemService;
import com.greenreuse.exchange.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:4173")
public class ItemController {
    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        var imageOpt = itemService.getImageById(id);
        if (imageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageOpt.get().getData());
    }

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems() {
        logger.info("Fetching all items");
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/available")
    public ResponseEntity<List<ItemDto>> getAvailableItems() {
        logger.info("Fetching available items");
        return ResponseEntity.ok(itemService.getAvailableItems());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ItemDto>> getItemsByCategory(@PathVariable Item.Category category) {
        logger.info("Fetching items by category: {}", category);
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<ItemDto>> getItemsByLocation(@PathVariable String location) {
        logger.info("Fetching items by location: {}", location);
        return ResponseEntity.ok(itemService.getItemsByLocation(location));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam String searchTerm,
            @RequestParam(required = false) String location) {

        logger.info("Searching items with term: {}, location: {}", searchTerm, location);
        if (location != null && !location.isEmpty()) {
            return ResponseEntity.ok(itemService.searchItemsByLocation(searchTerm, location));
        }
        return ResponseEntity.ok(itemService.searchItems(searchTerm));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        logger.info("Fetching item by ID: {}", id);
        try {
            return ResponseEntity.ok(itemService.getItemById(id));
        } catch (Exception e) {
            logger.error("Item not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createItem(
            @Valid CreateItemRequest request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            logger.info("Creating new item by user: {}", userEmail);
            ItemDto createdItem = itemService.createItem(request, userEmail);
            return ResponseEntity.ok(createdItem);
        } catch (Exception e) {
            logger.error("Failed to create item", e);
            return ResponseEntity.badRequest().body("Failed to create item: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createItemWithUpload(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("category") Item.Category category,
            @RequestParam("location") String location,
            @RequestParam("type") Item.Type type,
            @RequestParam(value = "exchangeFor", required = false) String exchangeFor,
            @RequestPart("images") MultipartFile[] images,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            logger.info("Creating item with images by user: {}", userEmail);
            ItemDto createdItem = itemService.createItemWithImages(
                    title, description, category, location, type, exchangeFor, images, userEmail);
            return ResponseEntity.ok(createdItem);
        } catch (Exception e) {
            logger.error("Failed to create item with images", e);
            return ResponseEntity.badRequest().body("Failed to upload item: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/claim")
    public ResponseEntity<?> claimItem(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            String email = authentication.getName();
            logger.info("Attempting to claim item {} by user {}", id, email);

            User user = (User) userService.loadUserByUsername(email);
            itemService.claimItem(id, user);

            logger.info("Successfully claimed item {}", id);
            return ResponseEntity.ok().build();

        } catch (jakarta.persistence.EntityNotFoundException ex) {
            logger.warn("Item not found with ID: {}", id);
            return ResponseEntity.status(404).body("Item not found");
        } catch (IllegalStateException ex) {
            logger.warn("Claim failed for item {}: {}", id, ex.getMessage());
            return ResponseEntity.status(409).body(ex.getMessage());
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            logger.warn("Access denied for item claim: {}", ex.getMessage());
            return ResponseEntity.status(403).body(ex.getMessage());
        } catch (Exception e) {
            logger.error("Failed to claim item {}", id, e);
            return ResponseEntity.badRequest().body("Failed to claim item: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/unclaim")
    public ResponseEntity<?> unclaimItem(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            String email = authentication.getName();
            logger.info("Attempting to unclaim item {} by user {}", id, email);

            User user = (User) userService.loadUserByUsername(email);
            itemService.unclaimItem(id, user);

            logger.info("Successfully unclaimed item {}", id);
            return ResponseEntity.ok().build();

        } catch (jakarta.persistence.EntityNotFoundException ex) {
            logger.warn("Item not found with ID: {}", id);
            return ResponseEntity.status(404).body("Item not found");
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            logger.warn("Access denied for item unclaim: {}", ex.getMessage());
            return ResponseEntity.status(403).body(ex.getMessage());
        } catch (Exception e) {
            logger.error("Failed to unclaim item {}", id, e);
            return ResponseEntity.badRequest().body("Failed to unclaim item: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            logger.info("Attempting to delete item {} by user {}", id, user.getEmail());
            itemService.deleteItem(id, user.getEmail());
            logger.info("Successfully deleted item {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to delete item {}", id, e);
            return ResponseEntity.badRequest().body("Failed to delete item: " + e.getMessage());
        }
    }

}