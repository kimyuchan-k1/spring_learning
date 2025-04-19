package com.example.springdatajpa.controller;

import com.example.springdatajpa.entity.Member;
import com.example.springdatajpa.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    /*@GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }*/

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

//    @GetMapping("/members")
//    public Page<Member> list(Pageable pageable) {
//        Page<Member> page = memberRepository.findAll(pageable);
//        return page;
//
//    }



    // http 요청 파라미터로 Pageable 을 넣을 수 있다.
    // pageDefault 어노테이션 으로 페이징 기준의 기본값을 설정함.
    /*@RequestMapping(value = "/members_page", method = RequestMethod.GET)
    public String list(@PageableDefault(size = 12,sort = "username",
    direction = Sort.Direction.DESC) Pageable pageable) {
        return "Success";
    }*/


    /**
     * Page를 바로 Dto 로 변환할 수있다.
     * Page 인터페이스는 map 함수로 바로 Page<MemberDto> 로 반환한다.
     */
    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }


    @Data
    public static class MemberDto {
        private Long id;
        private String username;

        public MemberDto(Member member) {
            this.id = member.getId();
            this.username = member.getUsername();
        }
    }


}

