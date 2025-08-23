package com.greenreuse.exchange.repository;

import com.greenreuse.exchange.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByClaimedById(Long userId);

    List<Transaction> findByItemId(Long itemId);
}
