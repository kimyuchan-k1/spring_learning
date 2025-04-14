package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 엔티티를 직접 파라미터에 받기
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member)
    {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }


    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.name);

        // 실제 회원가입
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);

    }


    @PutMapping("api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id
            ,@RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(),findMember.getName());

    }


    // 응답값으로 엔티티를 직접 외부에 노출한 V1 : 매우 안좋음.
    @GetMapping("api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    /**
     * 조회 V2 : 응답 값으로 엔티티를 직접 노출하지 않고 DTO로 변환하여 응답하기
     */

    @GetMapping("api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();

        // 엔티티 -> DTO 전환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }


    // 제네릭 클래스를 적용해 향후 필요한 필드를 감싸게 허용함.
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }



    @AllArgsConstructor
    @Data
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberRequest {

        private String name;
    }

    @Data

    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }

    }


}


