package hello.core.member;

public interface MemberService {

    // 회원가입기능
    void join(Member member);
    // 조회기능
    Member findMember(Long memberId);


}
