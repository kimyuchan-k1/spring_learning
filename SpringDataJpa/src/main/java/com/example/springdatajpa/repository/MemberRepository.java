package com.example.springdatajpa.repository;

import com.example.springdatajpa.dto.MemberDto;
import com.example.springdatajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {


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




}
