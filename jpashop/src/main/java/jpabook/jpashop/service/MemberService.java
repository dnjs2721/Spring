package jpabook.jpashop.service;

import jpabook.jpashop.service.dto.UpdateMemberRequestDto;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 전체 서비스에 적용, 읽기 전용
@RequiredArgsConstructor // final 이 있는 필드만 가지고 생성자를 만들어준다.
public class MemberService {

    private final MemberRepository memberRepository;

    /* @RequiredArgsConstructor 를 사용하지 않을 때
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    } */

    // 회원 가입
    @Transactional // 쓰기 작업에 따로 표시하여 읽기 전용 해제
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getMemberId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 수정
     */
    @Transactional
    public void update(Long id, UpdateMemberRequestDto request) {
        Member member = memberRepository.findById(id).get();

        member.setName(request.getName());
        member.setAddress(new Address(request.getCity(), request.getStreet(), request.getZipcode()));
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).get();
    }

}
