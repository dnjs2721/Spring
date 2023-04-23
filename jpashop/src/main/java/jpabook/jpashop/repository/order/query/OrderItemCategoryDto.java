package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class OrderItemCategoryDto {
    private String categoryName;
    @JsonIgnore
    private Long itemId;

    public OrderItemCategoryDto(String categoryName, Long itemId) {
        this.categoryName = categoryName;
        this.itemId = itemId;
    }
}
