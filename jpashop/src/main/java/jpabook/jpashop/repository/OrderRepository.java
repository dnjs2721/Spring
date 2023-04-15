package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void sava(Order order) {
        em.persist(order);
    }

    public Order findOne(Long orderId) {
        return  em.find(Order.class, orderId);
    }

//    public List<Order> findAll(OrderSearch orderSearch) {}

}
