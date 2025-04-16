package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * V1 . 엔티티 직접 노출
     *  - Hibernate5module 등록, lazy =null 처리
     *   - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for( Order order : all) {
            order.getMember().getName(); // lazy 강제 초기화
            order.getDelivery().getAddress(); // lazy 강제 초기화
        }

        return all;
    }

    /**
     *  엔티티를 조회해서 DTO로 변환하기
     *  - 단점 : 지연로딩으로 인해 쿼리 N번 호출
     */
    @GetMapping("api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2()   {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o-> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }

    /**
     * 엔티티를 조회해서 DTO로 변환하기( fetch join)
     * -fetch join 으로 1번 호출 -> fetch join 은 뭐냐?
     */

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }


    /**
     * JPA 에서 DTO로 바로 조회
     *  - 쿼리 1번 호출
     *  - select 절에서 원하는 데이터만 선택해서 조회
     *
     *
     * new 명령어를 사용해서 jpql의 결과를 DTO로 즉시 변환
     * 레포지토리 재사용성 떨어짐 -> 단기성 레포지토리
     * api 스펙에 맞춘 코드가 레포지토리에 들어가는 단점
     */

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }


}
