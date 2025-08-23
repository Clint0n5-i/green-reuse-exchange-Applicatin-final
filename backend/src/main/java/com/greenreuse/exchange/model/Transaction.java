package com.greenreuse.exchange.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    public static final String Type = null;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by", nullable = false)
    private User claimedBy;

    @Column(nullable = false)
    private LocalDateTime dateClaimed;

    @PrePersist
    protected void onCreate() {
        dateClaimed = LocalDateTime.now();
    }

    // Manual getters and setters since Lombok seems to have issues
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    
    public User getClaimedBy() { return claimedBy; }
    public void setClaimedBy(User claimedBy) { this.claimedBy = claimedBy; }
    
    public LocalDateTime getDateClaimed() { return dateClaimed; }
    public void setDateClaimed(LocalDateTime dateClaimed) { this.dateClaimed = dateClaimed; }

}
