package com.example.nplusone.service;


import com.example.nplusone.domain.Member;
import com.example.nplusone.dto.MemberDTO;
import com.example.nplusone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JdbcTemplate jdbcTemplate;


    /**
     * N+1 문제 발생 예시 -> 다대일 관계에서 단순 조회 -> 엔티티 조회
     */

    public void demoNPluOne() {
        System.out.println(">>> N+1 데모 시작: Member 전체 조회 후 Team 접근");

        List<Member> members = memberRepository.findAll();

        // 실제 사용하는 시점에 레이지 로딩으로 N개 가 호출됨.
        for (Member member : members) {
            System.out.println("Member: " + member.getName()
                    + ", Team: " + member.getTeam().getTeamName());
        }

        System.out.println(">>> N+1 데모 종료");

    }

    /**
     * Fetch join 으로 해결하기
     */

    public List<Member> getMembersFetchJoin() {

        List<Member> members = memberRepository.findAllWithTeamFetchJoin();
        System.out.println("쿼리 끝");

        for (Member member : members) {
            System.out.println("TeamName: " + member.getTeam().getTeamName());
        }
        return members;
    }

    /**
     * DTO 조회 방식
     */

    public List<MemberDTO> getMembersWithDto() {
        return memberRepository.findAllMemberDto();
    }

    /**
     * 4단계 : jdbc templete으로 sql 직접 작성하기
     */
    public List<Map<String,Object>> getMembersWithJdbc() {
        String sql  = """
                SELECT m.id as member_id,
                       m.name as member_name,
                       t.team_name as team_name
                    FROM member m
                    LEFT JOIN team t on m.team_id = t.id
                """;

        return jdbcTemplate.queryForList(sql);
    }
}

