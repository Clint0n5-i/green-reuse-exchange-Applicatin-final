package com.greenreuse.exchange.dto;

import com.greenreuse.exchange.model.Item;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String title;
    private String description;
    private Item.Type type;
    private String exchangeFor;
    private Item.Category category;
    private Item.Status status;
    private String location;
    private UserDto postedBy;
    private LocalDateTime createdAt;
    private Boolean claimed;
    private UserDto claimedBy;
    private List<Long> imageIds;

    public static ItemDto fromItem(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setCategory(item.getCategory());
        dto.setStatus(item.getStatus());
        dto.setLocation(item.getLocation());
        dto.setPostedBy(UserDto.fromUser(item.getPostedBy()));
        dto.setCreatedAt(item.getCreatedAt());
        dto.setClaimed(item.getClaimed());
        dto.setClaimedBy(item.getClaimedBy() != null ? UserDto.fromUser(item.getClaimedBy()) : null);
        if (item.getImages() != null) {
            dto.setImageIds(item.getImages().stream().map(img -> img.getId()).toList());
        }
        dto.setType(item.getType());
        dto.setExchangeFor(item.getExchangeFor());
        return dto;
    }

    // Manual getters and setters since Lombok seems to have issues
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Item.Category getCategory() {
        return category;
    }

    public void setCategory(Item.Category category) {
        this.category = category;
    }

    public Item.Status getStatus() {
        return status;
    }

    public void setStatus(Item.Status status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserDto getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(UserDto postedBy) {
        this.postedBy = postedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getClaimed() {
        return claimed;
    }

    public void setClaimed(Boolean claimed) {
        this.claimed = claimed;
    }

    public UserDto getClaimedBy() {
        return claimedBy;
    }

    public Item.Type getType() {
        return type;
    }

    public void setType(Item.Type type) {
        this.type = type;
    }

    public String getExchangeFor() {
        return exchangeFor;
    }

    public void setExchangeFor(String exchangeFor) {
        this.exchangeFor = exchangeFor;
    }

    public void setClaimedBy(UserDto claimedBy) {
        this.claimedBy = claimedBy;
    }

}
