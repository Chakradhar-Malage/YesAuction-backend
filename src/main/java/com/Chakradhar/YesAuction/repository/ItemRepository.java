package com.Chakradhar.YesAuction.repository;

import com.Chakradhar.YesAuction.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}