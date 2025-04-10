package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceimpl;
import hello.core.member.memoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    // 오직 외부에서만 구현체를 결정한다. -- 공연 기획자
    // 실행은 배우에게 맡긴다. Config 가 객체를 생성하고 연결하는 역할을 하고 Service 실행하는 역할을 하도록 정확히 분리했다.!!


    //생성자 주입 컨트롤 alt m  =  메서드 추출
    @Bean
    public MemberService memberService() {
        return new MemberServiceimpl(memberRepository());
    }
    @Bean
    public static memoryMemberRepository memberRepository() {
        return new memoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(),discountPolicy() );
    }
    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();

    }

}
