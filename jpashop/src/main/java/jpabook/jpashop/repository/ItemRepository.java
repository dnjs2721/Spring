package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void saveItem(Item item) {
        if (item.getItemId() == null) {
            em.persist(item);
        } else {
            // 병합
            // 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능
            // 엔티티를 값을 모두 교체(병합) 한다.
            // 병합시 원하는 속성만 선택해서 변경할 수 없으며, 없는 속성에 관해서는 null 로 업데이트 된다.
            // 최대한 쓰지말자. 변경감지를 사용
            em.merge(item);
        }
    }

    public Item findOne(Long itemId) {
        return em.find(Item.class, itemId);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
