package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.service.dto.UpdateMemberRequestDto;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /**
     * 등록 V1: 요청 값을 Member 엔티티를 직접 받는다.
     * 문제점
     * - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
     * - 실무에서는 회원 엔티티를 위한 API 가 다양하게 만들어지는데, 한 엔티티에 각각의 API 를 위한 요구사항을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * 결론
     * - API 요청 스펙에 맞추어 별도의 DTO 를 파라미터로 받는다.
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 조회 V1: 응답 값으로 엔티티를 직접 외부에 노출한다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     * - 응답 스펙을 맞추기 위한 로직이 추가된다.(@JsonIgnore, 별도의 뷰 로직 등등)
     * - 실무에서는 같은 엔티티에 대해 API 가 용도에 따라 다양하게 만들어지는데, 한 엔티티에 각각의 API 를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * - 추가로 컬렉션을 직접 반환하면 향후 API 스펙을 변경하기 어렵다, (별도의 Result 클래스 생성으로 해결)
     * 결론
     * - API 응답 스펙에 맞추어 별도의 DTO 를 반환한다.
     */
    // 조회 V1: 안 좋은 버전, 모든 엔티티가 노출, @JsonIgnore - 화면에 종속정이지 말자.
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    /**
     * 등록 V2: 요청 값으로 Member 엔티티 대신에 별도의 DTO 를 받는다.
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        member.setAddress(new Address(request.getCity(), request.getStreet(), request.getZipcode()));

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    /**
     * 조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO 를 반환한다.
     */
    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        //엔티티 -> DTO 변환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    /**
     * 수정 API
     */
    @PostMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long memberId,
            @RequestBody @Valid UpdateMemberRequestDto request) {

        memberService.update(memberId, request);
        Member findMember = memberService.findOne(memberId);

        return new UpdateMemberResponse(findMember.getMemberId(), findMember.getName());
    }

    /**
     * create Request DTO
     */
    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
        private String city;
        private String street;
        private String zipcode;
    }

    /**
     * find 응답 DTO
     * Result 클래스로 컬렉션을 감싸서 향후 필요한 필드를 추가할 수 있다.
     */
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    /**
     * create 응답 DTO
     */
    @Data
    @AllArgsConstructor
    static class CreateMemberResponse {
        private Long id;
    }

    /**
     * update 응답 DTO
     */
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }
}
