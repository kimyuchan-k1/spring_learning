package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class QuerydslApplicationTests {

    // Q타입이 정상 동작하는가? lombok이 정상 동작하는가?
    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = QHello.hello; // QueryDsl Q타입 동작 확인

        Hello result = query
                .selectFrom(qHello)
                .fetchOne();

        assertEquals(result,hello);

        // lombok 동작 확인
        assertEquals(result.getId(),hello.getId());
    }

    @PersistenceContext
    EntityManager em;



}
