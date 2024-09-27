package com.gema.thefirst.services;

import static com.gema.thefirst.constants.Constants.RECURSO_NAO_ENCONTRADO;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gema.thefirst.dto.OrderDTO;
import com.gema.thefirst.dto.OrderItemDTO;
import com.gema.thefirst.entities.Order;
import com.gema.thefirst.entities.OrderItem;
import com.gema.thefirst.entities.OrderStatus;
import com.gema.thefirst.entities.Product;
import com.gema.thefirst.entities.User;
import com.gema.thefirst.repositories.OrderItemRepository;
import com.gema.thefirst.repositories.OrderRepository;
import com.gema.thefirst.repositories.ProductRepository;
import com.gema.thefirst.services.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  @Autowired
  private OrderRepository repository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OrderItemRepository orderItemRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private AuthService authService;

  @Transactional(readOnly = true)
  public OrderDTO findById(Long id) {
    Order order = repository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException(RECURSO_NAO_ENCONTRADO));
    authService.validateSelfOrAdmin(order.getClient().getId());
    return new OrderDTO(order);
  }

  @Transactional
  public OrderDTO insert(OrderDTO dto) {

    Order order = new Order();

    order.setMoment(Instant.now());
    order.setStatus(OrderStatus.WAITING_PAYMENT);

    User user = userService.authenticated();
    order.setClient(user);

    for (OrderItemDTO itemDto : dto.getItems()) {
      Product product = productRepository.getReferenceById(itemDto.getProductId());
      OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
      order.getItems().add(item);
    }

    repository.save(order);
    orderItemRepository.saveAll(order.getItems());

    return new OrderDTO(order);
  }
}
