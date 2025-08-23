package com.greenreuse.exchange.repository;

import com.greenreuse.exchange.model.Item;
import com.greenreuse.exchange.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByStatus(Item.Status status);

    List<Item> findByCategory(Item.Category category);

    List<Item> findByLocation(String location);

    List<Item> findByCategoryAndLocation(Item.Category category, String location);

    List<Item> findByCategoryAndStatus(Item.Category category, Item.Status status);

    List<Item> findByLocationAndStatus(String location, Item.Status status);

    List<Item> findByCategoryAndLocationAndStatus(Item.Category category, String location, Item.Status status);

    @Query("SELECT i FROM Item i WHERE i.title LIKE %:searchTerm% OR i.description LIKE %:searchTerm%")
    List<Item> findByTitleOrDescriptionContaining(@Param("searchTerm") String searchTerm);

    @Query("SELECT i FROM Item i WHERE (i.title LIKE %:searchTerm% OR i.description LIKE %:searchTerm%) AND i.location = :location")
    List<Item> findByTitleOrDescriptionContainingAndLocation(@Param("searchTerm") String searchTerm,
            @Param("location") String location);

    List<Item> findByPostedBy(User user);

    List<Item> findByClaimedByAndClaimed(User user, Boolean claimed);

    List<Item> findByClaimed(Boolean claimed);
}
