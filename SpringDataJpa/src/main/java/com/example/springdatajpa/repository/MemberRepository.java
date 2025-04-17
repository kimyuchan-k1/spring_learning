package com.example.springdatajpa.repository;

import com.example.springdatajpa.dto.MemberDto;
import com.example.springdatajpa.entity.Member;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {


    // NamedQuery 사용하기 - username 파라미터에 username 인수를 넣고 Member를 자동으로 찾는다.
    List<Member> findByUsername(@Param("username") String username);

    //Query 어노테이션으로 정적 쿼리를 직접 작성하기
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();


    // dto로 직접 조회도 가능함.
    @Query("select new com.example.springdatajpa.dto.MemberDto(m.id, m.username, t.name) from Member m" +
            " join m.team t ")
    List<MemberDto> findMemberDto();


    // 페이징 - Page 반환 타입 -> count 쿼리 사용
    Page<Member> findByAge(int age, Pageable pageable);


    //이름과 나이를 기준으로 회원 조회
    public List<Member> findByUsernameAndAgeGreaterThanEqual(String username,int age);

    // in 절 사용하기
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);


    //count 쿼리 분리하기
    @Query(value = "select m from Member m",
    countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);

    /**
     *  data jpa 를 사용해 벌크성 수정 쿼리를 작성하기
     * 주의 : 수정 , 삭제는 @Modifying 엔티티를 추가해야 예외가 발생하지 않음.
     * 또한 벌크업 쿼리를 실행하고 나서 영속성 컨텍스트를 초기화 하기!!! 기존 값이 남아 있음.
     */
    @Query("update Member m set m.age = m.age +1 where m.age >= :age")
    @Modifying(clearAutomatically = true)
    int bulkAgePlus(@Param("age") int age);


    /**
     * jpql 없이 패치 조인을 data jpa 로 해결하기
     * EntityGraph 사용
     */

    // 공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();


    //메서드 이름으로 - 사실상 패치조인의 간편 버전!!!!
    @EntityGraph(attributePaths = {"team"})
    List<Member> findWithTeamByUsername(String username);


    // NamedEntityGraph 사용법

    @EntityGraph("Member.all")
    @Query("select m from Member m")
    List<Member> findMemberEntityGraphWithNamed();

    //QueryHint
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadonlyByUsername(String username);









}
