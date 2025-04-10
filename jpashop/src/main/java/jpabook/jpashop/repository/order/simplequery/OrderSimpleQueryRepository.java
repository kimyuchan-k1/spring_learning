package jpabook.jpashop.repository.order.simplequery;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;


    // 별개의 패키지로 구성한 이유:  특정 dto 를 jpa로 호출하는 repository 이기 때문에 독립적으로 명시했음.
    //데이터를 dto로 바로 조회하는 메서드를 작성.
    //dto 형식에 맞는 데이터만 select 하도록 쿼리를 작성한다.
    public List<OrderSimpleQueryDto> findOrderDtos(){
        return em.createQuery(
                "select new " +
                        "jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id,m.name," +
                        "o.orderDate,o.status,d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();

    }

}
