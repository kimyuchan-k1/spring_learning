package com.example.nplusone.domain;


import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

    // 양방향 연관관계 세팅 - 편의 메서드
    public void addMember(Member member) {
        this.members.add(member);
        member.setTeam(this);
    }

}
