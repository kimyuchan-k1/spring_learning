package hello.core.member;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class memoryMemberRepository implements MemberRepository {

    // map을 생성, id 와 member 로 저장하기 위해 ,간단한 메모리 생성, 동시성 이슈가 발생가능하지만 그냥 개발용 Hash map 을 사용하기
    private static Map<Long, Member> store = new HashMap<Long, Member>();


    // save 와 find by id 를 override

    @Override
    public void save(Member member) {
        store.put(member.getId(),member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }
}
