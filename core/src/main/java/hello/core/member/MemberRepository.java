package hello.core.member;


public interface MemberRepository {


    // 기능 : 저장 , id 로 찾기
    void save(Member member);

    Member findById(Long memberId);



}
