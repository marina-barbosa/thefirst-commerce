package com.gema.thefirst.repositories;

import com.gema.thefirst.entities.OrderItem;
import com.gema.thefirst.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
}
