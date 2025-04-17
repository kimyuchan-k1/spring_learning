package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {


    private final EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m",Member.class)
                .getResultList();
    }

    // id 로 찾기 -> 있을 수도 없을 수도 있고
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        // 없으면 -> null 로 만듬. -> ofNullable
        return Optional.ofNullable(member);

    }

    public long count() {
        // 단일 값
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }
    // 멤버엔티티 에서 id 에 맞는 회원 조회
    public Member find(Long id) {
        return em.find(Member.class, id );
    }


    // 페이징
    public List<Member> findByPage(int age, int offset,int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc ",Member.class)
                .setParameter("age",age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    // 단건조회 - 카운트
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age",Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    //이름과 나이를 기준으로 회원 조회 - 순수 jpa
    public List<Member> findByUserNameAndAgeGreaterThan(String username, int age) {
        // 로직 이름 기준 정렬 and 나이 기준 정렬 -> jpql
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username",username)
                .setParameter("age",age).getResultList();

    }

    //NamedQuery 사용하기!!
    public List<Member> findByUsername(String username) {
        List<Member> resultList =
                em.createNamedQuery("Member.findByUsername",Member.class)
                        .setParameter("username",username)
                        .getResultList();

        return resultList;


    }

}
