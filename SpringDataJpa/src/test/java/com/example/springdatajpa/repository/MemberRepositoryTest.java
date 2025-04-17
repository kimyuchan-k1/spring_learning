package com.example.springdatajpa.repository;

import com.example.springdatajpa.dto.MemberDto;
import com.example.springdatajpa.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
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

    // 페이징 테스트
    @Test
    public void page() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));


        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        // 페이지 유지하며 엔티티를 DTO로 변환하기
         page.stream().forEach(m -> {
            System.out.println("page m 이 뭘까??????????????????????????");
            System.out.println(m);
        });

        Page<MemberDto> dtopage = page.map(m -> new MemberDto(m.getId(),m.getUsername(),null));

        dtopage.stream().forEach(dto -> {
            System.out.println("------------------------------DTO ---------------------");
            System.out.println(dto);
        });

        //then
        List<Member> content = page.getContent();

        assertEquals(content.size(), 3); // 조회된 데이터
        assertEquals(page.getTotalElements(), 5); //전체 데이터 수
        assertEquals(page.getNumber(), 0); //페이지 번호
        assertEquals(page.getTotalPages(), 2); //전체 페이지 번호
        assertTrue(page.isFirst()); //첫 페이지 인가?
        assertTrue(page.hasNext()); // 다음 페이지가 있는가?



    }


        @Test
        public void findMemberWithUsernameAndAge() {
            // given - 회원이 있어야함.
            Member member1 = new Member("AAA",10);
            Member member2 = new Member("AAA",20);
            memberRepository.save(member1);
            memberRepository.save(member2);

            //when - 회원 조회
            List<Member> result = memberRepository.findByUsernameAndAgeGreaterThanEqual("AAA",15);


            //then
            assertEquals(result.size(),1);
            assertEquals(result.get(0).getUsername(),member2.getUsername());
            assertEquals(result.get(0).getAge(),20);
            assertTrue(result.get(0).getAge() > 15);

        }

        @Test
        @DisplayName("벌크성 수정 쿼리 - data jpa")
        public void bulkUpdate() throws Exception {
            memberRepository.save(new Member("member1",10));
            memberRepository.save(new Member("member2",19));
            memberRepository.save(new Member("member3",20));
            memberRepository.save(new Member("member4",21));
            memberRepository.save(new Member("member5",40));

            //when
            int resultCount = memberRepository.bulkAgePlus(20);

            //then
            assertEquals(resultCount, 3);
            assertEquals(memberRepository.findByUsername("member3").get(0).getAge(), 21);
        }






}