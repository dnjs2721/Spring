package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUserName();
    }

    /**
     * 도메인 클래스 컨버터
     * HTTP 요청은 회원 id 를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환
     * 도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음
     * 복잡한 처리에서는 부적합
     * 주의:
     * 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 한다.
     * (트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 면경해도 DB에 반영되지 않는다.
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUserName();
    }

    /**
     * 페이징과 정렬
     * 파라미터로 Pageable 을 받을 수 있다.
     * - Pageable 은 인터페이스, 실제로는  org.springframework.data.domain.PageRequest 객체 생성
     * 요청 파라미터
     * - 예) /members?page=0&size=3&sort=id,desc&sort=username,desc
     *  - page: 현제 페이지, 0부터 시작한다.
     *  - size: 한 페이지에 노출할 데이트 건수
     *  - sort: 정렬 조건을 정의한다
     *      - 예) 정렬 속성(ASC || DESC), 정렬 방향을 변경하고 싶으면 sort 파라미터 추가 (asc 생략 가능)
     * - 글로벌 설정은 yml 파일 수정
     */
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "userName", direction = Sort.Direction.DESC) Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }

//    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
