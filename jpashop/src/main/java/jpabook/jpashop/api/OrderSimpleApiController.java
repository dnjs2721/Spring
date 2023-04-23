package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderSearch;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member ManyToOne
 * Order -> Delivery OneToOne
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 (무한)-> 한쪽은 @JsonIgnore 설정해야 한다.
     * 엔티티 직접 노출 절대 금지
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }
    /**
     * V2. 엔티티를 조회해서 DTO 로 변환(fetch join 사용X)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     * - 지연 로딩으로 너무 많은 SQL 실행 SQL 실행 수
     * - - order 1번
     * - - member , address N번(order 조회 수 만큼)
     * - - orderItem N번(order 조회 수 만큼)
     * - - item N번(orderItem 조회 수 만큼)
     */
    @GetMapping("/api/v2/simple-orders")
    public List<OrderSimpleDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        List<OrderSimpleDto> collect = orders.stream()
                .map(o -> new OrderSimpleDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * V3: 엔티티를 DTO 로 변환 (fetch join 사용)
     * - fetch join 으로 쿼리 1번 호출
     */
    @GetMapping("/api/v3/simple-orders")
    public List<OrderSimpleDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<OrderSimpleDto> collect = orders.stream()
                .map(order -> new OrderSimpleDto(order))
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * V4. JPA 에서 DTO 로 바로 조회
     * - 쿼리 1번 호출
     * - select 절에서 원하는 데이터만 선택해서 조회
     * - 일반적인 SQL 을 사용할 때 처럼 원하는 값을 선택해서 조회
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDto();
    }

    @Data
    public class OrderSimpleDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public OrderSimpleDto(Order order) {
            orderId = order.getOrderId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
