package hello.login.domain.login;


import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;


    /*
     * @return null  로그인 실패
     */
    public Member login(String loginId, String password) {
        /*Optional<Member> findMemberOptional  = memberRepository.findByLoginId(loginID);
        Member member = findMemberOptional.get();

        if(member.getPassword().equals(password)) {
            return member;
        }

        if(findMemberOptional.get().getPassword().equals(password)) {
            return findMemberOptional.get();
        } else {
            throw new IllegalStateException("<UNK> <UNK> <UNK> <UNK>.");
        }
*/
        // optional 은 filter를 걸 수 있음.
        return memberRepository.findByLoginId(loginId)
                .filter( m -> m.getPassword().equals(password))
                .orElse(null);

    }

}
