package com.example.springdatajpa.repository;

import com.example.springdatajpa.dto.MemberDto;
import com.example.springdatajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {


    // NamedQuery 사용하기 - username 파라미터에 username 인수를 넣고 Member를 자동으로 찾는다.
    List<Member> findByUsername(@Param("username") String username);


    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();


    // dto로 직접 조회도 가능함.
    @Query("select new com.example.springdatajpa.dto.MemberDto(m.id, m.username, t.name) from Member m" +
            " join m.team t ")
    List<MemberDto> findMemberDto();
}
