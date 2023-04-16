package jpabook.jpashop.domain.order;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class OrderSearch {
    private String memberName;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
