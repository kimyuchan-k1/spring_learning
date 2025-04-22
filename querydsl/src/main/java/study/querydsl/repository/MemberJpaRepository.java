package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import study.querydsl.entity.Member;

import java.util.List;
import java.util.Optional;

import static study.querydsl.entity.QMember.member;

@Repository
public class MemberJpaRepository  {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    // 생성자 주입
    public MemberJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);

    }

    public void save(Member member) {
        em.persist(member);

    }

    public Optional<Member> findById(Long id) {
        Member findMember = em.find(Member.class,id);
        return Optional.ofNullable(findMember);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        return em.createQuery("select m from Member m" +
                " where m.username = :username", Member.class)
                .setParameter("username",username)
                .getResultList();

    }

    /**
     * querydsl 사용 -> 문자를 사용하지 않고 전부 코드로 작성함.!!! 실수 가능성 줄어들고 가독성이 좋아짐!!!
     */

    public List<Member> findAll_Querydsl() {
        return queryFactory.selectFrom(member).fetch();
    }

    public List<Member> findByUsername_Querydsl(String username) {
        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }
}
