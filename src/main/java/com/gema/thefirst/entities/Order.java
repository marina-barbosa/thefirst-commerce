package com.gema.thefirst.entities;

import java.time.Instant;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_order")
@Data
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
  private Instant moment;
  private OrderStatus status;

  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
  private Payment payment;

  @ManyToOne
  @JoinColumn(name = "client_id")
  private User client;

  @OneToMany(mappedBy = "id.order")
  private Set<OrderItem> items = new HashSet<>();

  public Order() {
  }

  public Order(Long id, Instant moment, OrderStatus status, User client) {
    this.id = id;
    this.moment = moment;
    this.status = status;
    this.client = client;
  }

  public Order(Long id, Instant moment, OrderStatus status, User client, Payment payment) {
    this.id = id;
    this.moment = moment;
    this.status = status;
    this.client = client;
    this.payment = payment;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Instant getMoment() {
    return moment;
  }

  public void setMoment(Instant moment) {
    this.moment = moment;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public User getClient() {
    return client;
  }

  public void setClient(User client) {
    this.client = client;
  }

  public Set<OrderItem> getItems() {
    return items;
  }

  public List<Product> getProducts() {
    return items.stream().map(x -> x.getProduct()).toList();
  }

  public Payment getPayment() {
    return payment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Order order = (Order) o;

    return Objects.equals(id, order.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

}
