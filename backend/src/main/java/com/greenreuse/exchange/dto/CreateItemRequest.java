package com.greenreuse.exchange.dto;

import com.greenreuse.exchange.model.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateItemRequest {

    @NotBlank(message = "Title is required")
    private String title;
    @NotNull(message = "Type is required")
    private Item.Type type = Item.Type.FREE;

    private String exchangeFor;

    private String description;

    @NotNull(message = "Category is required")
    private Item.Category category;

    @NotBlank(message = "Location is required")
    private String location;

    @NotEmpty(message = "At least one image is required")
    @Size(min = 1, max = 5, message = "Must provide between 1 and 5 images")
    private List<org.springframework.web.multipart.MultipartFile> images;

    // Manual getters and setters since Lombok seems to have issues
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

    public void setCategory(Item.Category category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
