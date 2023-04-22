package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.item.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemQueryDto {
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;
//    private List<OrderItemCategoryDto> itemCategories;
    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
