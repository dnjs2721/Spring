package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    /**
     * 컬렉션은 별도로 조회
     * Query: 루트 1번, 컬렉션 N 번 실행
     * - 이런 방식을 선택한 이유는 다음과 같다.
     * - ToOne 관계는 조인해도 데이터 row 수가 증가하지 않는다.
     * - ToMany 관계는 조인하면 row 수가 증가한다.
     * row 수가 증가하지 않는 ToOne 관계는 조인으로 최적화 하기 쉬우므로 한번에 조회하고,
     * ToMany 관계는 최적화 하기 어려우므로 findOrderItems() 같은 별도의 메서드로 조회한다.
     * 단건 조회에서 많이 사용하는 방식
     */
    public List<OrderQueryDto> findOrderQueryDto() {
        // 루트 조회 (ToOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();

        // 루프를 돌면서 컬렉션 추가 (추가 쿼리 실행)
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());

            // 루프를 돌면서 컬렉션 추가 (추가 쿼리 실행)
            orderItems.forEach(i -> {
                List<OrderItemCategoryDto> categories = findOrderItemCategories(i.getItemId());
                i.setItemCategories(categories);
            });
            o.setOrderItems(orderItems);
        });
        return result;
    }

    /**
     * 최적화
     * Query: 루트 1번, 컬렉션(orderItems) 1번, 컬렉션(category) 1번
     * 데이터를 한꺼번에 처리할 때 많이 사용하는 방식
     */
    public List<OrderQueryDto> findAllByDto_optimization() {
        // 루트 조회 (toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();

        // orderItem 컬렉션을 MAP 한방에 조회
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        // 루프를 돌면서 컬렉션 추가 (추가 쿼리 실행 X)
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        // toOrderIds 를 통해 얻은 식별자(orderId)로 ToMany 관계인 OrderItem 을 한꺼번에 조회 (IN)
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.orderId, i.itemId, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.orderId in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // item 의 categories 컬렉션을 MAP 한방에 조회
        Map<Long, List<OrderItemCategoryDto>> orderItemCategoryMap = findOrderItemCategoryMap(toOrderItemIds(orderItems));

        // orderItems 루프를 돌면서 카테고리 컬랙션 추가 (추가 쿼리 실행 X)
        orderItems.forEach(i -> i.setItemCategories(orderItemCategoryMap.get(i.getItemId())));

        /**
         * MAP 을 사용해서 매칭 성능 향상(O(1))
         * orderItems 루프를 돌면서
         * orderItemQueryDto 의 orderId를 기준으로 MAP 으로 바꾼다.
         * Key : orderId, Value : orderItemQueryDto
         */
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }

    private Map<Long, List<OrderItemCategoryDto>> findOrderItemCategoryMap(List<Long> orderItemsIds) {
        // toOrderItemIds 를 통해 얻은 식별자(itemId)로 ToMany 관계인 Category 를 한꺼번에 조회 (IN)
        List<OrderItemCategoryDto> orderItemCategories = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemCategoryDto(ca.name, ica.itemId)" +
                                " from Category ca" +
                                " join ca.items ica" +
                                " where ica.itemId in :itemIds", OrderItemCategoryDto.class)
                .setParameter("itemIds", orderItemsIds)
                .getResultList();

        /**
         * MAP 을 사용해서 매칭 성능 향상(O(1))
         * orderItemCategories 루프를 돌면서
         * orderItemCategoryDto 의 itemId를 기준으로 MAP 으로 바꾼다.
         * Key : itemId를, Value : orderItemCategoryDto
         */
        return orderItemCategories.stream()
                .collect(Collectors.groupingBy(OrderItemCategoryDto::getItemId));
    }

    private static List<Long> toOrderItemIds(List<OrderItemQueryDto> orderItems) {
        // 컬렉션 orderItems 에서 orderItem 만 선택하여 리스트로 생성
        List<Long> orderItemsIds = orderItems.stream()
                .map(i -> i.getItemId())
                .collect(Collectors.toList());
        return orderItemsIds;
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> result) {
        // 컬렉션 result 에서 orderId 만 선택하여 리스트로 생성
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }


    /**
     * V4
     * 1:N 관계인 orderItems 조회
     */
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.orderId, i.itemId, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.orderId = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    /**
     * V4
     * N:N 관계인 category 조회
     */
    private List<OrderItemCategoryDto> findOrderItemCategories(Long itemId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemCategoryDto(ca.name, ica.itemId)" +
                " from Category ca" +
                " join ca.items ica" +
                " where ica.itemId = :itemId", OrderItemCategoryDto.class)
                .setParameter("itemId", itemId)
                .getResultList();
    }


    /**
     * V4, V5
     * 1:N 관계(컬렉션)를 제외한 나머지를 한번에 조회
     */
    public List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    /**
     * V6
     *
     */
    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.itemId ,i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
