package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.dto.UpdateItemDto;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;
    private String name;
    private int price;
    private int stockQuantity;
    @ManyToMany
    @JoinTable(name = "Item_Category",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories = new ArrayList<>();

    //==연관관계 편의 메서드==//
    public void addCategory(Category category) {
        this.categories.add(category);
        category.getItems().add(this);
    }

    //==비즈니스 로직==//

    /** 재고 증가 */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /** 재고 감소 */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고 부족");
        }
        this.stockQuantity = restStock;
    }

    /** 수정 */
    public void changeItem(UpdateItemDto dto) {
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.stockQuantity = dto.getStockQuantity();
    }
}
