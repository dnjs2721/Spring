package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;
    private String name;
    @ManyToMany(mappedBy = "categories")
    private List<Item> items = new ArrayList<>();
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 편의 메서드==//

    public void addItem(Item item) {
        this.items.add(item);
        item.getCategories().add(this);
    }

    public void addChildCategory(Category child) {
        child.setParent(this);
        this.child.add(child);
    }
}
