package com.gema.thefirst.entities;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_payment")
@Data
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE") // salva sendo UTC
  private Instant moment;

  @OneToOne
  @MapsId // Essa chave vai ser tanto primary key quanto foreign key
  private Order order;

  public Payment() {
  }

  public Payment(Long id, Instant moment) {
    this.id = id;
    this.moment = moment;
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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Payment payment = (Payment) o;

    return Objects.equals(id, payment.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

}
