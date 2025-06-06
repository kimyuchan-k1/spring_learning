package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Team;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamJpaRepository {

    private final EntityManager em;

    public Team save(Team team) {
        em.persist(team);
        return team;

    }

    public void delete(Team team) {
        em.remove(team);
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t",Team.class)
                .getResultList();
    }

    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class,id);
        return Optional.ofNullable(team);
    }

    public long count() {
        // 조회한 것들 개수 세서 리턴
        return em.createQuery("select count(t) from Team t",Long.class)
                .getSingleResult();
    }

}
