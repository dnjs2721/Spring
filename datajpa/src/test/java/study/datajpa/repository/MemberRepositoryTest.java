package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired
    EntityManager em;
    @Test
    public void testMember() throws Exception {
        //given
        Member member = new Member("memberA");

        //when
        Member saveMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(saveMember.getMemberId()).get();

        //then
        assertThat(findMember.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());

        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCrud() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getMemberId()).get();
        Member findMember2 = memberRepository.findById(member2.getMemberId()).get();

        // 단건 조회 검증
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void testNamedQuery() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> result = memberRepository.findByUserName("AAA");
        Member findMEmber = result.get(0);

        //then
        assertThat(findMEmber).isEqualTo(m1);
    }

    @Test
    public void testQuery() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);

        //then
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void findUserNameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> userNameList = memberRepository.finduserNameList();
        for (String s : userNameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);

        m1.setTeam(team);

        List<MemberDto> findMemberDto = memberRepository.findMemberDto();
        for (MemberDto memberDto : findMemberDto) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> names = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member name : names) {
            System.out.println("name = " + name);
        }
    }
    @Test
    public void returnTypeTest() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        /**
         * 컬렉션 조회
         */
        List<Member> aaa = memberRepository.findListByUserName("AAA");
        List<Member> aad = memberRepository.findListByUserName("AAd");
        System.out.println("aaa = " + aaa);
        System.out.println("aad = " + aad); // null 반환이 아닌 빈 컬렉션 반환

        /**
         * 단건 조회
         * 결과가 2건 이상 javax.persistence.NonUniqueResultException 예외 발생
         */
        Member aaa1 = memberRepository.findMemberByUserName("AAA");
        Member aad1 = memberRepository.findMemberByUserName("AAd");
        System.out.println("aaa1 = " + aaa1);
        System.out.println("aad1 = " + aad1); // null 반환

        Optional<Member> aaa2 = memberRepository.findOptionalByUserName("AAA");
        Optional<Member> aad2 = memberRepository.findOptionalByUserName("AAd");
        System.out.println("aaa2 = " + aaa2);
        System.out.println("aad2 = " + aad2); // Optional.empty 반환
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        // Pageable 의 구현체
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // 페이지를 유지하면서 엔티티틀 DTO 로 변환하기
        Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getMemberId(), null, m.getUserName()));

        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent(); // 조회된 데이터
        List<Member> content2 = slice.getContent();
//        long totalElements = page.getTotalElements();
//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); // 전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); // 첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?

        // slice 는 totalCount 가 날아오지 않는다.
        assertThat(content2.size()).isEqualTo(3);
        assertThat(slice.getNumber()).isEqualTo(0);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();

    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        // 벌크 연산후에는 영속성 컨텍스트에 남아있기 때문에 변경 전 값이 나온다.
        // 벌크 연산 후에 조회를 하기 전에는 영속성 컨텍스트를 초기화 하자.
        List<Member> result = memberRepository.findByUserName("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        //given
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));

        Member memberA = memberRepository.save(new Member("memberA", 10, teamA));
        Member memberB = memberRepository.save(new Member("memberA", 10, teamB));

        em.flush();
        em.clear();

        //when
        // 멤버만 가지고 오고 팀은 프록시 객체로 가지고 온다.
//        List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findEntityGraphByUserName("memberA");

        //then
        for (Member member : members) {
            System.out.println("member = " + member);
            // 팀을 호출 하는 순간 sql 이 나간다. 프록시 초기화 (N + 1 문제)
            // fetch 조인으로 해결
            System.out.println("member.getTeam().getTeamName() = " + member.getTeam().getTeamName());
        }
    }

    @Test
    public void queryHint() throws Exception {
        //given
        Member member1 = new Member("member1, 10");
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findById(member1.getMemberId()).get();
        findMember.setUserName("member2");

        em.flush(); // Update Query 실행X

        //then

    }
}