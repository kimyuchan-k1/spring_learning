package com.example.nplusone.repository;


import com.example.nplusone.domain.Member;
import com.example.nplusone.dto.MemberDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {


    /**
     * 단순 조회 -> N+1 발생상황
     */
    List<Member> findAll();


    /**
     * Fetch Join
     *
     */
    @Query("SELECT m FROM Member m JOIN FETCH m.team")
    List<Member> findAllWithTeamFetchJoin();

    /**
     * DTO로 직접 조회 (JPQL)
     */
    @Query("SELECT new com.example.nplusone.dto.MemberDTO(m.id, m.name, t.teamName)" +
            " FROM Member m join m.team t")
    List<MemberDTO> findAllMemberDto();





}
