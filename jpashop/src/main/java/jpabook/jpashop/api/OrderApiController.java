package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.item.Category;
import jpabook.jpashop.domain.order.Order;
import jpabook.jpashop.domain.order.OrderItem;
import jpabook.jpashop.domain.order.OrderSearch;
import jpabook.jpashop.domain.order.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.groupingBy;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * V1. 엔티티 직접 노출
     * - 엔티티가 변하면 API 스펙이 변한다.
     * - 트랜잭션 안에서 지연 로딩 필요
     * - 양방향 연관관계 문제
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 연관관계면 무한 루프에 걸리지 않게 한곳에 @JsonIgnore 를 추가해야 한다. -> 따로 추가하지 않아 오류 발생
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getDeliveryId();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO 로 변환 (fetch join 사용 x)
     * - 트렌젝션 안에서 지연 필요
     * - 지연 로딩으로 너무 많은 SQL 실행
     * - - order 1번
     * - - member, address N 번 (order 조회 수 만큼)
     * - - orderItem N 번 (order 조회 수 만큼)
     * - - item N 번 (orderItem 조회 수 만큼)
     * - - category N 번 (item 조회 수 만큼)
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(toList());
        return collect;
    }

    /**
     * V3. 엔티티를 조회해서 DTO 로 뱐환 (fetch join 사용 O)
     * - 페이징 시에는 N 부분을 포기해야 함 (대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경 가능)
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream().map(order -> new OrderDto(order))
                .toList();
        return result;
    }

    /**
     * V3.1. 엔티티를 조회해서 DTO 로 뱐환 페이징을 고려
     * - ToOne 관계만 우선 모두 패치 조인으로 최적화
     * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize 로 최적화
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithItemPaging(offset, limit);
        List<OrderDto> result = orders.stream().map(order -> new OrderDto(order))
                .toList();
        return result;
    }

    /**
     * V4.JPA 에서 DTO 로 바로 조회, 컬렉션 N 조회 (1+NQuery)
     * - 페이징 가능
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDto();
    }

    /**
     * V5.JPA 에서 DTO 로 바로 조회, 컬렉션 1 조회 최적화 버전 (1+1Query)
     * - 페이징 가능
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

     /**
      * V6. JPA 에서 DTO 로 바로 조회, 플랫 데이터(1Query) (1 Query)
      * 단점
      * - 페이징 불가능...
      * - 쿼리는 한번이지만 조인으로 인해 DB 에서 애플리케이션에 전달하는 데이터에 중복 데이터가 추가되므로 상황에 따라 V5 보다 더 느릴 수 도 있다.
      * - 애플리케이션에서 추가 작업이 크다.
     */
     @GetMapping("/api/v6/orders")
     public List<OrderQueryDto> ordersV6() {
         List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

         return flats.stream()
                 .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                         mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                 )).entrySet().stream()
                 .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(),
                         e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                 .collect(Collectors.toList());
     }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getOrderId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;
        private List<CategoryDto> categories;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
            categories = orderItem.getItem().getCategories().stream()
                    .map(category -> new CategoryDto(category))
                    .toList();
        }
    }

    @Data
    static class CategoryDto {
        private String categoryName;

        public CategoryDto(Category category) {
            categoryName = category.getName();
        }
    }
}
