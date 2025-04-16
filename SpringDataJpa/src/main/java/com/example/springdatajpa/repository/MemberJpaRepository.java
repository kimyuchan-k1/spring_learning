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
}
