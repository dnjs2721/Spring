package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamB);
        em.persist(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();
    }

    @Test
    public void startJPQL() throws Exception {
        //member1을 찾아라
        String qlString =
                "select m from Member m " +
                "where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
//        QMember m = new QMember("m");
//        QMember m = QMember.member;

        Member findMember = queryFactory
                .select(member) // static Import QMember
                .from(member)
                .where(member.username.eq("member1")) // 파라미터 바인딩
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member) // select, from 을 selectFrom 으로 합칠 수 있음
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();
//        member.username.eq("member1") // username = 'member1'
//        member.username.ne("member1") //username != 'member1'
//        member.username.eq("member1").not() // username != 'member1'

//        member.username.isNotNull() //이름이 is not null

//        member.age.in(10, 20) // age in (10,20)
//        member.age.notIn(10, 20) // age not in (10, 20)
//        member.age.between(10,30) //between 10, 30

//        member.age.goe(30) // age >= 30
//        member.age.gt(30) // age > 30
//        member.age.loe(30) // age <= 30
//        member.age.lt(30) // age < 30

//        member.username.like("member%") //like 검색
//        member.username.contains("member") // like ‘%member%’ 검색
//        member.username.startsWith("member") //like ‘member%’ 검색

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() {
        List<Member> findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(10))
                .fetch();
        // where() 에 파라미터로 검색조건을 추가하면 AND 조건이 추가된다.
        // 이 경우 null 값은 무시 -> 메서드 추출을 활용해서 동적 쿼리를 깔끔하게 만들 수 있음

        assertThat(findMember.size()).isEqualTo(1);
    }

    /**
     * 결과 조회
     * fetch(): 리스트 조회, 데이터 없으면 빈 리스트 반환
     * fetchOne(): 단 건 조회
     *  - 결과가 없으면 null
     *  - 결과가 둘 이상이면 com.querydsl.core.NonUniqueResultException -> Exception 반환
     * fetchFirst(): limit(1).fetchOne()
     * fetchResults(): 페이징 정보 포함, total count 쿼리 추가 실행
     * fetchCount(): count 쿼리로 변경해서 count 수 조회
     */
    @Test
    public void resultFetchTest(){
        // List
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();


        Member fetchOne = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        /**
         * fetchResults() deprecated at 5.0.0
         * fetch() 를 사용하자
         */
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        results.getTotal();
        List<Member> content = results.getResults();

        /**
         * fetchCount() deprecated at 5.0.0
         * fetch().size() 를 사용하자
         */
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    /**
     * 정렬
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순
     * 2. 회원 이름 올림차순
     * 단 2에서 회원 이름이 없으면 마지막 출력(nulls last)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    /**
     * 페이징
     * 조회 건수 제한
     */
    @Test
    public void paging1(){

        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 0 부터 시작(zero index)
                .limit(2) // 최대 2건 조회
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    /**
     * fetchResults(), fetchCount() 대신
     * fetch() 를 사용
     */
    public Page<Member> paging(Pageable pageable) {
        List<Member> content = queryFactory
                .selectFrom(member)
                .offset(pageable.getOffset()) // offset
                .limit(pageable.getPageSize()) // limit
                .fetch();

        int totalSize = queryFactory // count 쿼리
                .selectFrom(member)
                .fetch().size();

        return new PageImpl<>(content, pageable, totalSize); // 쿼리 결과로 페이징 객체 리턴
    }
}