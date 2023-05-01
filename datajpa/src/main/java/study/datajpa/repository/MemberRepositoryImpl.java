package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;

// 사용자 정의 인터페이스 구현 클래스

/**
 * 사용자 정의 인터페이스 구현 클래스는 규칙을 지켜야 한다.
 * a. 리포지토리 인터페이스 이름 + Impl -> MemberRepositoryImpl
 * b. 사용자 정의 인테페이스 명 + Impl 방식도 지원한다. -> MemberRepositoryCustomImpl
 * a 방식 보다 b 방식이 사용자 인터페이스 이름과 구현 클래스 이름이 비슷하므로 더 직관적이다.
 * 추가로 여러 인터페이스를 분리해서 구현하는 것도 가능하기 떄문에 b 방식을 사용하는 것을 더 권장한다.
 * 스프링 데이터 JPA 가 인식해서 스프링 빈으로 등록
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
