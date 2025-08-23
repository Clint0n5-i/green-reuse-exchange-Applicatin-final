package com.greenreuse.exchange.service;

import com.greenreuse.exchange.dto.CreateItemRequest;
import com.greenreuse.exchange.dto.ItemDto;
import com.greenreuse.exchange.model.Item;
import com.greenreuse.exchange.model.User;
import com.greenreuse.exchange.model.ItemImage;
import com.greenreuse.exchange.model.Transaction;
import com.greenreuse.exchange.repository.ItemRepository;
import com.greenreuse.exchange.repository.TransactionRepository;
import com.greenreuse.exchange.repository.UserRepository;
import com.greenreuse.exchange.repository.ItemImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemImageRepository itemImageRepository;

    public java.util.Optional<ItemImage> getImageById(Long id) {
        return itemImageRepository.findById(id);
    }

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Item retrieval methods
    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemDto::fromItem)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getAvailableItems() {
        return itemRepository.findByStatus(Item.Status.AVAILABLE).stream()
                .map(ItemDto::fromItem)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItemsByCategory(Item.Category category) {
        return itemRepository.findByCategory(category).stream()
                .map(ItemDto::fromItem)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItemsByLocation(String location) {
        return itemRepository.findByLocation(location).stream()
                .map(ItemDto::fromItem)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String searchTerm) {
        return itemRepository.findByTitleOrDescriptionContaining(searchTerm).stream()
                .map(ItemDto::fromItem)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItemsByLocation(String searchTerm, String location) {
        return itemRepository.findByTitleOrDescriptionContainingAndLocation(searchTerm, location).stream()
                .map(ItemDto::fromItem)
                .collect(Collectors.toList());
    }

    // Item creation methods
    public ItemDto createItem(CreateItemRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = new Item();
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setLocation(request.getLocation());
        item.setPostedBy(user);
        item.setStatus(Item.Status.AVAILABLE);
        item.setType(request.getType());
        item.setExchangeFor(request.getExchangeFor());

        // Handle image upload from request (assume request.getImages() returns
        // List<MultipartFile>)
        if (request.getImages() == null || request.getImages().isEmpty()) {
            throw new IllegalArgumentException("At least one image is required");
        }
        if (request.getImages().size() > 5) {
            throw new IllegalArgumentException("No more than 5 images allowed");
        }
        List<ItemImage> images = new ArrayList<>();
        for (org.springframework.web.multipart.MultipartFile file : request.getImages()) {
            try {
                ItemImage image = new ItemImage();
                image.setData(file.getBytes());
                image.setItem(item);
                images.add(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to process image: " + file.getOriginalFilename(), e);
            }
        }
        item.setImages(images);

        Item savedItem = itemRepository.save(item);
        return ItemDto.fromItem(savedItem);
    }

    public ItemDto createItemWithImages(String title, String description,
            Item.Category category, String location, Item.Type type, String exchangeFor, MultipartFile[] images,
            String userEmail) {

        // Validate inputs
        if (images == null || images.length == 0) {
            throw new IllegalArgumentException("At least one image is required");
        }

        // Set up upload directory
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadPath, e);
        }

        // Process and store images
        List<ItemImage> imageEntities = new ArrayList<>();
        for (MultipartFile file : images) {
            if (file.isEmpty())
                continue;

            // Validate file type and size
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }
            if (file.getSize() > 10_000_000) { // 10MB
                throw new IllegalArgumentException("File size exceeds 10MB limit: " + file.getOriginalFilename());
            }

            try {
                ItemImage image = new ItemImage();
                image.setData(file.getBytes());
                imageEntities.add(image);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to process image: " + file.getOriginalFilename(), ex);
            }
        }

        if (imageEntities.isEmpty()) {
            throw new RuntimeException("No valid images were uploaded");
        }

        // Create and save item
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setCategory(category);
        item.setLocation(location);
        item.setPostedBy(user);
        item.setStatus(Item.Status.AVAILABLE);
        item.setType(type);
        item.setExchangeFor(type == Item.Type.EXCHANGE ? exchangeFor : null);
        for (ItemImage image : imageEntities) {
            image.setItem(item);
        }
        item.setImages(imageEntities);

        Item savedItem = itemRepository.save(item);
        return ItemDto.fromItem(savedItem);
    }

    // Item claim/unclaim methods
    @Transactional
    public void claimItem(Long itemId, User user) {
        if (user == null) {
            throw new AccessDeniedException("User must be authenticated");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        // Prevent self-claiming
        if (item.getPostedBy().equals(user)) {
            throw new IllegalStateException("You cannot claim your own item");
        }

        if (item.getStatus() == Item.Status.CLAIMED) {
            throw new IllegalStateException("Item is already claimed");
        }

        // Only update claim fields
        item.setClaimed(true);
        item.setClaimedBy(user);
        item.setStatus(Item.Status.CLAIMED);
        // imageUrls is not modified here
        itemRepository.save(item);
        // Create a transaction record
        Transaction transaction = new Transaction();
        transaction.setItem(item);
        transaction.setDateClaimed(LocalDateTime.now());
        transaction.setClaimedBy(user);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void unclaimItem(Long itemId, User user) {
        if (user == null) {
            throw new AccessDeniedException("User must be authenticated");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        if (!item.getStatus().equals(Item.Status.CLAIMED)) {
            throw new IllegalStateException("Item is not currently claimed");
        }

        if (!user.equals(item.getClaimedBy())) {
            throw new AccessDeniedException("You cannot unclaim an item claimed by someone else");
        }

        // Only update claim fields
        item.setClaimed(false);
        item.setClaimedBy(null);
        item.setStatus(Item.Status.AVAILABLE);
        itemRepository.save(item);

        // Delete the transaction for this item and user
        List<Transaction> transactions = transactionRepository.findByItemId(itemId);
        for (Transaction transaction : transactions) {
            if (transaction.getClaimedBy().getId().equals(user.getId())) {
                transactionRepository.delete(transaction);
            }
        }
    }

    // Item deletion
    @Transactional
    public void deleteItem(Long itemId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        if (!item.getPostedBy().equals(user)) {
            throw new AccessDeniedException("Not authorized to delete this item");
        }

        itemRepository.delete(item);
    }

    // Single item retrieval
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        return ItemDto.fromItem(item);
    }
}