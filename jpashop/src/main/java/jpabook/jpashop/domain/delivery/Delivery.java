package jpabook.jpashop.domain.delivery;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;
    @Embedded
    private Address address;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    @OneToOne(fetch = LAZY, mappedBy = "delivery")
    private Order order;
}
