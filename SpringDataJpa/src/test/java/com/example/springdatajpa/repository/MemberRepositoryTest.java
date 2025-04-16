package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("테스트#1: 멤버 등록하고 찾기")
    public void findMember() {
        // 멤버 A 등록
        Member member = Member.builder().username("memberA").build();

        //repository 에 등록
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertNotNull(savedMember);
        assertNotNull(findMember);
        assertEquals(savedMember.getId(),findMember.getId());
        assertEquals(savedMember.getUsername(),findMember.getUsername());

    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertEquals(findMember1,member1);
        assertEquals(findMember2,member2);

        // 전체 조회 검증

        List<Member> all = memberRepository.findAll();
        assertEquals(all.size(),2);

        // 삭제 검증

        memberRepository.delete(member1);
        memberRepository.delete(member2);


        long deletedCount = memberRepository.count();
        assertEquals(deletedCount,0);



    }


}