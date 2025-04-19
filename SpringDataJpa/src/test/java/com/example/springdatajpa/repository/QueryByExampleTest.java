package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import com.example.springdatajpa.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Example 의 장점
 * 1. 동적 쿼리를 편리하게 처리
 * 2. 도메인 객체를 그대로 사용
 * 3. 데이터 저장소를 RDB 에서 NOSQL로 변경해도 코드 변경이 없게 추상화 됨.
 * 4. JpaRepository 인터페이스에 이미 포함
 *
 * 단점
 * 1. 조인은 가능하지만 내부 조인 만 가능 Inner Join 만 가능
 * 2. 중첩 제약 조건은 안됨.
 * 3. 매칭 조건이 매우 단순
 *
 * 실무에서 사용하깅 매칭조건이 너무 단순 and Left 조인도 안됨.
 * -> Query Dsl 을 사용하자.
 */
@SpringBootTest
@Transactional
public class QueryByExampleTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    public void basic() throws Exception {
        //given
        Team teamA = Team.builder().name("teamA").build();
        em.persist(teamA);

        Member m1 = new Member("m1",0, teamA);
        Member m2 = new Member("m2",0, teamA);

        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        //Prove
        Member member = new Member("m1");
        Team team = Team.builder().name("teamA").build();

        member.setTeam(team);


        //ExampleMacher 생성, age 프로퍼티 무시
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        // Probe 와 ExampleMather 로 구성 -> age 무시하고 teamA가ㅏ 속한 m1에 대한 Example
        Example<Member> example = Example.of(member,matcher);

        List<Member> result = memberRepository.findAll(example);

        //then
        Assertions.assertEquals(result.size(),1);


    }
}
