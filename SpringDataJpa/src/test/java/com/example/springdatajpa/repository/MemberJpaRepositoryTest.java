package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;


    @Test
    public void testMember() {
        Member member = Member.builder().username("memberA").build();
        Member savedMember = memberJpaRepository.save(member);

        Member foundMember = memberJpaRepository.find(savedMember.getId());

        // junit5
        // Null 이 아니어야 함.
        assertNotNull(savedMember);
        // memberA로 저장되어야 함.
        assertEquals("memberA",savedMember.getUsername());
        // 찾은 멤버는 null 이 아님
        assertNotNull(foundMember);
        // found 와 save의 값은 같아야함.
        assertEquals(savedMember.getId(),foundMember.getId());
        assertEquals(savedMember.getUsername(),foundMember.getUsername());

    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertEquals(findMember1,member1);
        assertEquals(findMember2,member2);

        // 전체 조회 검증

        List<Member> all = memberJpaRepository.findAll();
        assertEquals(all.size(),2);

        // 삭제 검증

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);


        long deletedCount = memberJpaRepository.count();
        assertEquals(deletedCount,0);



    }

    @Test
    public void paging() throws Exception {

        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));


        int age =10;
        int offset = 0;
        int limit =3;

        //when
        List<Member> members = memberJpaRepository.findByPage(age,offset,limit);
        long totalCount = memberJpaRepository.totalCount(age);


        //페이징 계산 공식
        // totalPage = totalCount /size

        assertEquals(members.size(),3);
        assertEquals(totalCount,5);
    }

    @Test
    @DisplayName("findByUserNameAndAgeGreaterThan")
    public void 회원이름_나이_회원조회하기() {
        // given - 회원이 있어야함.
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("AAA",20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //when - 회원 조회
        List<Member> result = memberJpaRepository.findByUserNameAndAgeGreaterThan("AAA",15);


        //then
        assertEquals(result.size(),1);
        assertEquals(result.get(0).getUsername(),member2.getUsername());
        assertEquals(result.get(0).getAge(),20);
        assertTrue(result.get(0).getAge() > 15);

    }
}