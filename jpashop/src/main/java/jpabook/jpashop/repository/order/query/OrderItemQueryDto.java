package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderItemQueryDto {
    @JsonIgnore
    private Long orderId;
    private Long itemId;
    private String itemName;
    private int orderPrice;
    private int count;
    private List<OrderItemCategoryDto> itemCategories;
    public OrderItemQueryDto(Long orderId, Long itemId,String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
