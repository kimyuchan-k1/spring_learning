package study.querydsl;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory

    // 테스트 전 무조건 실행!!!! BeforeEach!!
    @BeforeEach
    public void before() {
        // given


        queryFactory = new JPAQueryFactory(em);


        Team teamA = Team.builder().name("teamA").build();
        Team teamB = Team.builder().name("teamB").build();
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",20,teamA);
        Member member3 = new Member("member3",30,teamB);
        Member member4 = new Member("member4",40,teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

    }

    /**
     * JPQL 은 문자로 작성하고 QueryDsl은 코드로 작성함.
     * jpql 은 런타임에러 , querydsl은 컴파일 에러를 반환함. -> 에러를 더 빨리 반환한다? 메리트있음.!!!
     * 실수 할 가능성이 적음 ~ 왜? String 이 아니니까
     * jpql 은 파라미터 바인딩을 직접, querydsl은 파라미터 바인딩 자동처리함.
     */

    @Test
    public void startJPQL() {
        //member1 을 찾아라

        // when
        String qlString  =
                "select m from Member m" +
                        " where m.username = :username";
        Member findMember = em.createQuery(qlString,Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        //then
        assertEquals(findMember.getUsername(),"member1");

    }

    @Test
    public void startQuerydsl() {
        //member1 을 찾아라
        //when
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // 멤버 테이블 선언
        QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1")) // 조건
                .fetchOne(); //단건 조회
        assertEquals(findMember.getUsername(),"member1");
    }
}
