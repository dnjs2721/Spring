package jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    /**
     * new 명령어를 사용해서 JPQL 의 결과를 DTO 로 즉시 변환
     * SELECT 절에서 원하는 데이터를 직접 선택하므로 DB -> 애플리케시션 네트워크 용량 최적화(생각보다 미비)
     * 리포지토리 재사용성 떨어짐, API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점
     * 화면에 종속적이라 따로 분리
     */
    public List<OrderSimpleQueryDto> findOrderDto() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name,o.orderDate, o.status, d.address)" +
                                " From Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
