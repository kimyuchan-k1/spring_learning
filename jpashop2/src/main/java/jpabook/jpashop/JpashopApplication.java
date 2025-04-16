package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {


    // 초기화된 프록시 객체만 노출, 초기화 되지 않은 프록시 객체 노출 X

    /**
     * 엔티티를 직접 노출하는 api를 작성할 때는 양방향 연관 관계를 갖는 한 곳에 @JsonIgnore 처리를 해주어야 한다.
     * 안 그러면 서로 양쪽을 호출하면서 무한 순환 참조를 하게 될 것이다.
     * 가장 좋은 방법은 엔티티를 dto로 변환하여 처리하는 것이다.
     */
    @Bean
    Hibernate5JakartaModule hibernate5JakartaModule() {
        return new Hibernate5JakartaModule();
    }



    public static void main(String[] args) {
        SpringApplication.run(JpashopApplication.class, args);
    }

}





