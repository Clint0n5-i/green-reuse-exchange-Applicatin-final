package com.greenreuse.exchange.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDashboardDto {
    private UserDto user;
    private List<ItemDto> postedItems;
    private List<ItemDto> claimedItems;
    private int totalPostedItems;
    private int totalClaimedItems;
    private int availableItems;
    private int claimedItemsCount;

    public static UserDashboardDto create(UserDto user, List<ItemDto> postedItems, List<ItemDto> claimedItems) {
        UserDashboardDto dashboard = new UserDashboardDto();
        dashboard.setUser(user);
        dashboard.setPostedItems(postedItems);
        dashboard.setClaimedItems(claimedItems);
        dashboard.setTotalPostedItems(postedItems.size());
        dashboard.setTotalClaimedItems(claimedItems.size());
        dashboard.setAvailableItems(
                (int) postedItems.stream().filter(item -> "AVAILABLE".equals(item.getStatus().name())).count());
        dashboard.setClaimedItemsCount(
                (int) postedItems.stream().filter(item -> "CLAIMED".equals(item.getStatus().name())).count());
        return dashboard;
    }

    // Manual getters and setters since Lombok seems to have issues
    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<ItemDto> getPostedItems() {
        return postedItems;
    }

    public void setPostedItems(List<ItemDto> postedItems) {
        this.postedItems = postedItems;
    }

    public List<ItemDto> getClaimedItems() {
        return claimedItems;
    }

    public void setClaimedItems(List<ItemDto> claimedItems) {
        this.claimedItems = claimedItems;
    }

    public int getTotalPostedItems() {
        return totalPostedItems;
    }

    public void setTotalPostedItems(int totalPostedItems) {
        this.totalPostedItems = totalPostedItems;
    }

    public int getTotalClaimedItems() {
        return totalClaimedItems;
    }

    public void setTotalClaimedItems(int totalClaimedItems) {
        this.totalClaimedItems = totalClaimedItems;
    }

    public int getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(int availableItems) {
        this.availableItems = availableItems;
    }

    public int getClaimedItemsCount() {
        return claimedItemsCount;
    }

    public void setClaimedItemsCount(int claimedItemsCount) {
        this.claimedItemsCount = claimedItemsCount;
    }
}
