package jpabook.jpashop.api;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.order.query.OrderFlatDto;
import jpabook.jpashop.order.query.OrderItemQueryDto;
import jpabook.jpashop.order.query.OrderQueryDto;
import jpabook.jpashop.repository.OrderQueryRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    private final OrderQueryRepository orderQueryRepository;


    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1()  {
        // 바로 엔티티를 직접 조회함.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        // Lazy 강제 초기화
        for (Order order : orders) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());

        }

        return orders;

    }

    @GetMapping("api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(order ->
                    new OrderDto(order)
                ).toList();

        return result;


    }



    /**
     *
     * 1대 N 관계에서
     * 패치 조인 걸기
     */
    @GetMapping("api/v3/orders")
    public List<OrderDto> ordersV3()     {
        List<Order> orders = orderRepository.findAllWithTeam();
        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .toList();

        return result;
    }


    /**
     * 페이징 고려하여 설계
     *
     */


    @GetMapping("api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0")
                                            int offset,
                                        @RequestParam(value = "limit", defaultValue = "100")
                                        int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .toList();
        return result;

    }

    /**
     * jpa 에서 DTO로 직접 조회 - 1:N 에서 루트를 도니까 어쨋든 쿼리를 N 번 부르게 됨.
     */

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    /**
     *  1대 다에서 N번 쿼리 를 날리는 것을 최적화하기
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }


    /**
     * 플랫 데이터 최적화
     */

    @GetMapping("api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream().collect(groupingBy(o -> new OrderQueryDto(o.getOrderId()
                , o.getName(), o.getOrderDate(), o.getOrderStatus()
                , o.getAddress())
                , mapping(o -> new OrderItemQueryDto(o.getOrderId(),o.getItemName(),o.getOrderPrice(),o.getCount()),toList())))
                .entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId() , e.getKey().getName(), e.getKey().getOrderDate() , e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .toList();
        }

    }

    @Data
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }




}
