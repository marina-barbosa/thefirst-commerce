package com.gema.thefirst.repositories;

import com.gema.thefirst.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
