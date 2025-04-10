package hello.core.member;

import hello.core.AppConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {
    MemberService memberService;

    @BeforeEach
    public void beforeEach() {
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
    }


    @Test
    void join(){
        //Given
        Member member = new Member("memberA", 1L ,Grade.VIP);

        //When 등록, 조회 테스트
        memberService.join(member);
        Member findMember = memberService.findMember(1L );
        //then

        Assertions.assertThat(member).isEqualTo(findMember);


    }


}
