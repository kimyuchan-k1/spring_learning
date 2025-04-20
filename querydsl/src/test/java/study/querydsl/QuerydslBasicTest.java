package study.querydsl;


import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
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
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

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

        // 멤버 테이블 선언 - 별칭 지정
        QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1")) // 조건
                .fetchOne(); //단건 조회
        assertEquals(findMember.getUsername(),"member1");
    }

    // q 인스턴스를 static 으로 import 하고 사용하기
    @Test
    public void startQuerydsl3() {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertEquals(findMember.getUsername(),"member1");

    }

    /**
     *  and 조건 사용하기 where( ~~ ,~~)  콤마가 and 역할을 대신함.
     */
    @Test
    public void searchAndParam() {

        List<Member> result1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"), member.age.eq(10))
                .fetch();

        assertEquals(result1.size(),1);
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 desc
     * 2. 회원 이름 asc
     * 2에서 이름이 없으면 마지막에 출력 (nulls last)
     */


    @Test
    public void sort() {
        em.persist(new Member(null,100));
        em.persist(new Member("member5",100));
        em.persist(new Member("member6",100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(),member.username.asc().nullsLast())
                .fetch();
        Member member5 = result.get(0); //member5
        Member member6 = result.get(1); //member6
        Member member7 = result.get(2); // null


        assertEquals(member5.getUsername(), "member5");
        assertEquals(member6.getUsername(), "member6");
        assertNull(member7.getUsername());

    }

    /**
     * 페이징
     * -- 자동으로 Pageable 한 pageRequest 를 생성해서 보냄.
     */

    @Test
    public void paging() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 0부터 시작 (zero index)
                .limit(2) // 최대 2건
                .fetch();
        assertEquals(result.size(),2);
    }

    // 전체 조회건수

    @Test
    public void paging1() {
        List<Member> content = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        long total = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();
        assertEquals(content.size(), 2); // 페이징된 결과의 크기 확인
        assertEquals(total, 4); // 전체 회원 수 확인




    }

    /**
     * 집합함수 With Jpql
     * select
     *  COUNT(m)
     *  SUM(m.age)
     *  AVG(m.age)
     *  MAX(m.age)
     *  MIN(M.AGE)
     * from Member m
     *
     * -> QueryDsl 로
     */

    @Test
    public void aggregation() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertEquals(tuple.get(member.count()),4);
        assertEquals(tuple.get(member.age.sum()),100);
        assertEquals(tuple.get(member.age.avg()),25);
        assertEquals(tuple.get(member.age.max()),40);
        assertEquals(tuple.get(member.age.min()),10);


    }

    /**
     * 팀의 이름과 각 팀의 평균 연령 구하기
     * groupBy -> 파라미터 값 기준으로 그룹화
     * having -> 그룹의 제약 조건
     *
     * join(조인 대상, 별칭으로 사용할 Q타입)
     */


    @Test
    public void group() throws Exception {
        List<Tuple> result =queryFactory
                .select(team.name,member.age.avg())
                .from(member)
                .join(member.team,team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertEquals(teamA.get(team.name),"teamA");
        assertEquals(teamA.get(member.age.avg()),15);

        assertEquals(teamB.get(team.name),"teamB");
        assertEquals(teamB.get(member.age.avg()),35);
    }

    //팀 A 에 소속된 모든 회원 조회
    @Test
    public void join() throws Exception {
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<Member> result = queryFactory.
                select(member)
                .from(member)
                .join(member.team,team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertAll(
                () -> assertEquals("member1", result.get(0).getUsername()),
                () -> assertEquals("member2",result.get(1).getUsername())
        );

    }

    /**
     * 세타 조인(연관관계가 없는 필드로 조인)
     */

    @Test
    public void theta_join() throws Exception {
        em.persist(Member.builder().username("teamA").build());
        em.persist(Member.builder().username("teamB").build());

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();
        assertAll(
                () -> assertEquals("teamA", result.get(0).getUsername()),
                () -> assertEquals("teamB", result.get(1).getUsername())

        );
    }


    /**
     * on 절을 활용한 join
     */

    @Test
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team,team).on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }



}
