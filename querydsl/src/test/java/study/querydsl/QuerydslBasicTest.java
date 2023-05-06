package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;
import java.util.PrimitiveIterator;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;
    @PersistenceUnit
    EntityManagerFactory emf;

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
     * fetch() 를 사용 */
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
    @Test
    public void aggregation() throws Exception {
        //given
        List<Tuple> result = queryFactory
                .select(member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();
        //when
        Tuple tuple = result.get(0);
        //then
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @Test
    public void group() throws Exception {
        //given
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .orderBy(team.name.asc())
                .fetch();

        //when
        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        //then
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * 기본 조인
     * 조인의 기본 문법은 첫 번째 파라미터에 조인 대상을 지정하고, 두 번째 파라미터에 별칭으로 사용할 Q 타입을 지정하면 된다.
     * 팀A에 소속된 모든 회원
     */
    @Test
    public void join() throws Exception {
        //given
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * 세타 조인
     * 연관관계가 없는 필드로 조인
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    public void theta_join() throws Exception {
        //given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team) // from 절에 여러 엔티티를 선택해서 세타 조인, 외부 조인 불가능 (on절 사용시 가능)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");

    }
    /**
     * 조인 ON 절
     * ON 절을 활용한 조인
     *  - 조인 대상 필터링
     *  - 연관관계 없는 엔티티 외부 조인
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조회, 회원은 모두 조회
     */
    @Test
    public void join_on_filtering() throws Exception {
        //given
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 연관관께 없는 엔티티 외부 조인
     * 회원의 이름과 팀의 이름이 같은 대상 외부 조인
     */
    @Test
    public void join_on_no_relation() throws Exception {
        //given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name)) // 일반 조인과 다르게 엔티티 하나만 들어간다.
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
    /**
     * 조인 - 페치 조인
     * 페치 조인은 SQL 에서 제공하는 기능은 아니다. SQL 조인을 활용해서 연관된 엔티티를 SQL 한번에 조회하는 기능이다
     * 주로 성능 최적화에 사용하는 방법이다.
     */
    /* 페치 조인 미적용, 자연로딩으로 Member, TEAM SQL 쿼리 각각 실행 */
    @Test
    public void fetchJoinNo() throws Exception {
        //given
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // emf.getPersistenceUnitUtil().isLoaded -> 이미 로딩된 엔티티인지 검증
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("패치 조인 미적용").isFalse();
    }

    /* 페치 조인 적용 */
    @Test
    public void fetchJoinUse() throws Exception {
        //given
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        // emf.getPersistenceUnitUtil().isLoaded -> 이미 로딩된 엔티티인지 검증
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("패치 조인 적용").isTrue();
    }

    /**
     * 서브 쿼리
     * com.querydsl.jpa.JPAExpressions 사용
     * from 절의 서브쿼리 한계
     * - JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다.
     * from 절의 서브쿼리 해결방안
     * - 1. 서브쿼리를 join 으로 변경한다 (가능한 상황도 있고, 불가능한 상황도 있다.)
     * - 2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
     * - 3. nativeSQL 을 사용한다.
     */
    /* 서브 쿼리 eq 사용, 나이가 가장 많은 회원 조회 */
    @Test
    public void subQuery() throws Exception {
        //given
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub) // 밖의 member 가 중복되면 안되기에 QMember 생성
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(40);
    }
    /* 서브 쿼리 goe 사용, 나이가 평균 나이 이상인 회원 */
    @Test
    public void subQueryGoe() throws Exception {
        //given
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub) // 밖의 member 가 중복되면 안되기에 QMember 생성
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(30, 40);
    }
    /**
     * 서브쿼리 여러 건 처리 IN 사용
     */
    @Test
    public void subQueryIn() throws Exception {
        //given
        QMember memberSub = new QMember("memberSub"); // 밖의 member 가 중복되면 안되기에 QMember 생성

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(member.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40);
    }
    /* select 절에 서브쿼리 */
    @Test
    public void selectSubQuery() throws Exception {
        //given
        QMember memberSub = new QMember("memberSub"); // 밖의 member 가 중복되면 안되기에 QMember 생성

        List<Tuple> result = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("username = " + tuple.get(member.username));
            System.out.println("age = " +
                    tuple.get(JPAExpressions
                            .select(memberSub.age.avg())
                            .from(memberSub)));
        }
    }

    /**
     * Case 문
     * select, where 에서 사용 가능
     */
    // 단순한 조건
    @Test
    public void basicCase() throws Exception {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    // 복잡한 조건
    @Test
    public void complexCase() throws Exception {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21살~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    /**
     * orderBy 에서 Case 문 함께 사용하기
     * 예를 들어서 다음과 같은 임의의 순서로 회원을 출력하고 싶다면?
     * 1. 0 ~ 30살이 아닌 회원을 가장 먼저 출려
     * 2. 0 ~ 20살 회원 출력
     * 3. 21 ~ 30살 회원 출력
     */
    @Test
    public void orderByWithCase() throws Exception {
        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then(2)
                .when(member.age.between(21, 30)).then(1)
                .otherwise(3);

        List<Tuple> result = queryFactory
                .select(member.username, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc())
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            Integer rank = tuple.get(rankPath);
            System.out.println("username = " + username + " age = " + age + " rank = " + rank);
        }
    }

    /**
     * 상수, 문자 더하기
     * 상수가 필요하면 Expressions.constant(xxx) 사용
     */
    @Test
    public void constant() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
    /**
     * 문자 더하기 concat
     * 문자가 아닌 다른 타입은 stringValue() 로 문자로 변환할 수 있다.
     * 이 방법은 ENUM 을 처리할 때도 자주 사용한다.
     */
    @Test
    public void concat() throws Exception {
        String result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        System.out.println("result = " + result);
    }
    /**
     * 프로젝션과 결과 반환 - 기본
     * 프로젝션 : select 대상 지정
     * 프로젝션 대상이 하나
     *  - 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
     *  - 프로젝션 대상이 둘 이상이면 튜플이나 DTO 로 조회
     */
    // 프로젝션 대상이 하나.
    @Test
    public void simpleProjection() throws Exception {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();
        List<Member> result2 = queryFactory
                .selectFrom(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
        for (Member member1 : result2) {
            System.out.println("member1 = " + member1);
        }
    }

    // 튜플 조회, 프로젝션 대상이 둘 이상일 때 사용
    // com.querydsl.core.Tuple
    // repository 단에서 사용하는 건 괜찮지만 서비스나, 컨트롤러 계층까지 가면 좋지 못한 설계이다. -> DTO 변환 해서 반환.
    @Test
    public void tupleProjection() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }

    /**
     * 프로젝션 결과 반환 - DTO 조회
     * 순수 JPA 에서 DTO 조회
     * - new 명령어를 사용해야만 함
     * - DTO 의 package 이름을 다 적어줘야해서 지저분함
     * - 생성자 방식만 지원함
     */
    @Test
    public void findDtoByJPQL() throws Exception {
        List<MemberDto> result = em.createQuery("select new study.querydsl.dto.MemberDto(m.username, m.age) " +
                        "from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
    /**
     * 프로젝션 결과 반환 - DTO 조회
     * Querydsl 빈 생성 (Bean population)
     * 결과를 DTO 반환할 때 사용, 다음 3가지 방법 지원
     * - 프로퍼티 접근
     * - 필드 직접 접근
     * - 생성자 사용
     */
    // 프로퍼티 접근 - setter
    @Test
    public void findDtoBySetter() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // 필드 직접 접근
    @Test
    public void findDtoByField() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 별칭이 다를 때
     * 프로터티나, 필드 접근 생성 방식에서 이름이 다를 때 해결 방안
     * ExpressionUtils.as(source,alias) : 필드나, 서브 쿼리에 별칭 적용
     * username.as("name") : 필드에 별칭 적용
     */
    @Test
    public void findUserDtoByAnotherAlias() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                                member.username.as("name"), // UserDto 와 맞춰 주기
                                ExpressionUtils.as( // 필드나, 서브 쿼리에 별칭 적용
                                        JPAExpressions // 서브쿼리
                                                .select(memberSub.age.max())
                                                .from(memberSub), "age")
                        )
                ).from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    // 생성자
    @Test
    public void findDtoByConstructor() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class, // 생성자는 이름이 아닌 타입으로 판별하기 때문에 UserDto 도 사용가능
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
}