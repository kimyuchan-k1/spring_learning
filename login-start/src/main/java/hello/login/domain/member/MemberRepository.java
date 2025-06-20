package hello.login.domain.member;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberRepository {

    // 임의 의 member 저장 db
    private static Map<Long,Member> store = new HashMap<Long,Member>();
    // GeneratedValue PK 역할
    private static long sequence = 0L;


    public Member save(Member member) {
        // 회원의 id 기록
        member.setId(sequence++);
        log.info("save member :{}", member);
        store.put(member.getId(), member);
        return member;
    }

    // find by id
    public Member findById(Long id) {
        return store.get(id);
    }



    public List<Member> findAll() {
        // store 의 값들을 list 로 리턴하기
        return new ArrayList<Member>(store.values());
    }

//    public Member findByLoginId(String loginId) {
//        List<Member> members = findAll();
//        for( Member member : members) {
//            if( member.getLoginId().equals(loginId)) {
//                return Optional.of(member);
//            }
//        }
//
//        // 아무것도 없을 시? 그냥 null 리턴
//        return Optional.empty();
//
//
//    }

    public Optional<Member> findByLoginId(String loginId) {

        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();

    }

    public void clearStore() {
        store.clear();
    }
}
