package com.example.springdatajpa.repository;

import com.example.springdatajpa.dto.MemberDto;
import com.example.springdatajpa.entity.Member;
import com.example.springdatajpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
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

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

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
        assertEquals(savedMember.getId(), findMember.getId());
        assertEquals(savedMember.getUsername(), findMember.getUsername());

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

        assertEquals(findMember1, member1);
        assertEquals(findMember2, member2);

        // 전체 조회 검증

        List<Member> all = memberRepository.findAll();
        assertEquals(all.size(), 2);

        // 삭제 검증

        memberRepository.delete(member1);
        memberRepository.delete(member2);


        long deletedCount = memberRepository.count();
        assertEquals(deletedCount, 0);


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

        Page<MemberDto> dtopage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

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
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when - 회원 조회
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThanEqual("AAA", 15);


        //then
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getUsername(), member2.getUsername());
        assertEquals(result.get(0).getAge(), 20);
        assertTrue(result.get(0).getAge() > 15);

    }

    @Test
    @DisplayName("벌크성 수정 쿼리 - data jpa")
    public void bulkUpdate() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        //then
        assertEquals(resultCount, 3);
        assertEquals(memberRepository.findByUsername("member3").get(0).getAge(), 21);
    }

    @Test
    public void findMemberLazy() throws Exception {
        // given
        // member 1 -> teamA
        // member 2 -> teamB
        Team teamA = Team.builder().name("teamA").build();
        Team teamB = Team.builder().name("teamB").build();
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1",10,teamA));
        memberRepository.save(new Member("member2",20,teamB));

        em.flush();
        em.clear();;

        // lazyLoding 전
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {

            String result = Hibernate.isInitialized(member.getTeam()) ? "지연로딩 !!!" : "지연로딩 전~~~~";
            System.out.println(result);
        }

        // lazy loading 후
        for (Member member : members) {
            member.getTeam().getName();
            // lazyLoading 된 건지 확인하기!
            String result = Hibernate.isInitialized(member.getTeam()) ? "지연로딩 !!!" : "지연로딩 전~~~~";
            System.out.println(result);


        }

        // 지연로딩 X - 패치조인 테스트
        List<Member> result = memberRepository.findMemberEntityGraphWithNamed();
        // 로그확인하면 패치조인으로 한번에 sql 로 조회하는 것을 확인할 수 있다.

    }

    @Test
    @DisplayName("쿼리 힌트 테스트")
    public void queryHint() throws Exception {
        //given
        memberRepository.save(new Member("member1",10));
        em.flush();
        em.clear();

        //when
        Member member = memberRepository.findReadonlyByUsername("member1");
        member.setUsername("member2");


        // 원래는 member를 영속성 컨텍스트에 저장하고 변경 된 것을 감지하여
        // flush 할 때 update query를 날림. QueryHint 로 readonly를 설정한 member  엔티티는 업데이트 되 지않음
        em.flush();


        List<Member> result = memberRepository.findMemberCustom();

    }


    // Auditing 구현 테스트
    @Test
    public void JpaEventBaseEntity() throws Exception {
        //given
        Member member = new Member("member1");
        memberRepository.save(member);

        Thread.sleep(1000);
        member.setUsername("member2");

        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.createDate = " +
                findMember.getCreatedDate());
        System.out.println("findMember.updatedDate = " +
                findMember.getUpdatedDate());

    }
}


