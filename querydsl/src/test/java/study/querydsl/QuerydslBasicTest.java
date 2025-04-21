package study.querydsl;


import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.apache.catalina.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
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

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

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
        String qlString =
                "select m from Member m" +
                        " where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        //then
        assertEquals(findMember.getUsername(), "member1");

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
        assertEquals(findMember.getUsername(), "member1");
    }

    // q 인스턴스를 static 으로 import 하고 사용하기
    @Test
    public void startQuerydsl3() {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertEquals(findMember.getUsername(), "member1");

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

        assertEquals(result1.size(), 1);
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 desc
     * 2. 회원 이름 asc
     * 2에서 이름이 없으면 마지막에 출력 (nulls last)
     */


    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
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
        assertEquals(result.size(), 2);
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
        assertEquals(tuple.get(member.count()), 4);
        assertEquals(tuple.get(member.age.sum()), 100);
        assertEquals(tuple.get(member.age.avg()), 25);
        assertEquals(tuple.get(member.age.max()), 40);
        assertEquals(tuple.get(member.age.min()), 10);


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
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertEquals(teamA.get(team.name), "teamA");
        assertEquals(teamA.get(member.age.avg()), 15);

        assertEquals(teamB.get(team.name), "teamB");
        assertEquals(teamB.get(member.age.avg()), 35);
    }

    //팀 A 에 소속된 모든 회원 조회
    @Test
    public void join() throws Exception {
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<Member> result = queryFactory.
                select(member)
                .from(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertAll(
                () -> assertEquals("member1", result.get(0).getUsername()),
                () -> assertEquals("member2", result.get(1).getUsername())
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
     * 내부조인은 where로 해결하고 외부 조인의 경우는 join 의 필터링을 on으로 해결하자!!
     */

    @Test
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 연관관계 없는 엔티티 외부 조인
     * jpa 연관관계 없이 직접 테이블을 조인하는 방식 -> SQL 로 데이터를 다루는 작업
     */

    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(Member.builder().username("teamA").build());
        em.persist(Member.builder().username("teamB").build());

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("t = " + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    /**
     * 패치 조인 미적용
     *
     */
    @Test
    public void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();


        boolean loaded =
                emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertFalse(loaded);
    }

    /**
     * 패치 조인 적용 -> 즉시 연관관계에 해당하는 엔티티를 전부 조회
     */
    @Test
    public void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();
        System.out.println("Member = " + findMember);

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertTrue(loaded);


    }

    /**
     * 서브 쿼리
     * 나이가 가장 많은 회원 조회 -> JPAExpressions 라이브러리 사용
     */

    @Test
    public void subQuery() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertEquals(result.get(0).getAge(), 40);
    }

    @Test
    public void subQueryGoe() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertAll(
                () -> assertTrue(result.stream().allMatch(member -> member.getAge() >= 25)),
                () -> assertEquals(2, result.size()) // 평균 나이 이상인 멤버 수 확인
        );

    }

    @Test
    public void subQueryIn() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertAll(
                () -> assertTrue(result.stream().allMatch(member1 ->
                        member1.getAge() > 10
                ))
        );

        // select 절 에 sub쿼리 적용하기


        List<Tuple> fetch = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("username = " + tuple.get(member.username));
            System.out.println("age = " +
                    tuple.get(select(memberSub.age.avg())
                            .from(memberSub)));
        }


    }


    /**
     * 프로젝션 결과 반환 - 기본
     * 프로젝션 대상이 하나  -> 해당 타입으로 조회
     * 프로젝션 대상이 둘 -> 튜플이나 DTO로 조회
     */


    @Test
    public void Dto조회() {

        QMember memberSub = new QMember("memberSub");

        // jpa 로 dto 조회
        List<MemberDto> result = em.createQuery(
                "select new study.querydsl.dto.MemberDto(m.username , m.age)" +
                        " from Member m", MemberDto.class
        ).getResultList();

        // querydsl로 dto 반환
        // setter 로 접근 하기 -> 프로퍼티 접근

        List<MemberDto> resultUsingProperty = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        // 필드로 접근

        List<MemberDto> resultUseField = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        // 프로퍼티나 필드 접근 생성 방식에서 이름이 다를 때 -> ExpressionUtils.as(source,alias) 별칭을 부여
        // sub쿼리로는 JPAExpressions 사용 , 별칭부여는 ExpressionUtils 를 사용


        List<UserDto> fetch = queryFactory
                .select(Projections.fields(UserDto.class,
                                member.username.as("name"),
                                ExpressionUtils.as(
                                        JPAExpressions
                                                .select(memberSub.age.max())
                                                .from(memberSub), "age"
                                )
                        )
                ).from(member)
                .fetch();

        //생성자 사용

        List<MemberDto> resultUseConsruct = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

    }


}
