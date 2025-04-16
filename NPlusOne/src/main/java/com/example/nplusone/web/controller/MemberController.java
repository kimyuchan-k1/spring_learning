package com.example.nplusone.web.controller;


import com.example.nplusone.domain.Member;
import com.example.nplusone.dto.MemberDTO;
import com.example.nplusone.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/n-plus-one")
    public String demoNPlusOne() {
        memberService.demoNPluOne();
        return "N+1 Demo - Check console";
    }

    @GetMapping("/fetch-join")
    public List<Member> getMembersFetchJoin() {
        return memberService.getMembersFetchJoin();
    }

    @GetMapping("/dto")
    public List<MemberDTO> getMembersDto() {
        return memberService.getMembersWithDto();
    }

    @GetMapping("/jdbc")
    public List<Map<String, Object>> getMembersJdbc() {
        return memberService.getMembersWithJdbc();
    }

}
