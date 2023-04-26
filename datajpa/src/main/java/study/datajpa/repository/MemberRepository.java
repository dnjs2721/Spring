package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 많이 쓰는 방식 1
     * 간단할 때 사용
     */
    List<Member> findByUserNameAndAgeGreaterThan(String username, int age);
    List<Member> findTop3HelloBy();

    /**
     * NamedQuery 보다 아래 방식을 더 많이 사용한다.
     */
    @Query(name = "Member.findByUserName") // 엔티티의 NamedQuery 를 찾는다. 생략해도 동작
    List<Member> findByUserName(@Param("username") String username);

    /**
     * 많이 쓰는 방식 2
     * 오타가 있으면 애플리케이션 로딩 시점에 오류가 발생
     * 복잡할 때 사용
     */
    @Query("select m from Member m where m.userName = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int ange);

    @Query("select m.userName from Member m")
    List<String> finduserNameList();

    /**
     * 많이 쓰는 방식 3
     * Dto 로 직접 조회
     * Dto 로 직접 조회를 하기 위해서는 new 명령어가 필요하다.
     */
    @Query("select new study.datajpa.dto.MemberDto(m.memberId, m.userName, t.teamName) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * 많이 쓰는 방식 4
     * 컬렉션 파라미터 바인딩 IN
     */
    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    /**
     * 스프링 데이터 JPA 는 유연한 반환 타입 지원
     */
    List<Member> findListByUserName(String username); // 컬렉션
    Member findMemberByUserName(String username); // 단건
    Optional<Member> findOptionalByUserName(String username); // 단건 Optional

    // Pageable 은 인터페이스이다. 따라서 실제 사용할 때는 해당 인터페이스를 구현하는 구현체가 필요하다.

    /**
     * 카운트 쿼리 분리(이건 복잡한 sql 에서 사용, 데이터는 left join, 카운트는 left join 안해도 됨)
     * 성능으로 이어지기 때문에 매우 중요
     */
    @Query(value = "select m from Member m left join  m.team t",
            countQuery = "select count(m.userName) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    // Slice (Count X) 추가로 limit + 1 을 조회한다. 그래서 다음 페이지 여부 확인(게시글 더보기 기능)
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    /**
     * 벌크 연산
     * 벌크성 수정, 삭제 쿼리는 @Modifying 어노테이션을 사용는
     * clearAutomatically = true 벌크 연산 후 영속성 컨텍스트 초기화 = em.clear()
     * 권장하는 방안
     * 1. 영속성 컨텍스트 엔티티가 없는 상태에서 벌크 연산을 먼저 실행한다.
     * 2. 부득이하게 영속성 컨텍스트에 엔티티가 있으면 벌크 연산 직후 영속성 컨텍스트를 초기화 한다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}