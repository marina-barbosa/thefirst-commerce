package com.gema.thefirst.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gema.thefirst.dto.OrderDTO;
import com.gema.thefirst.services.OrderService;

import java.net.URI;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
  @GetMapping(value = "/{id}")
  public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
    OrderDTO dto = orderService.findById(id);
    return ResponseEntity.ok(dto);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<OrderDTO> insert(@Valid @RequestBody OrderDTO orderDTO) {
    orderDTO = orderService.insert(orderDTO);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(orderDTO.getId()).toUri();
    return ResponseEntity.created(uri).body(orderDTO);
  }
}
