package com.example.nplusone.dto;


import lombok.Data;

@Data
public class MemberDTO {
    private Long memberId;
    private String memberName;
    private String teamName;

    public MemberDTO(Long memberId, String memberName, String teamName) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.teamName = teamName;
    }
}
