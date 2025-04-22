package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberTeamDto {
    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;


    // querydsl 에서 dto 사용하기 위해 어노테이션 지정
    @QueryProjection
    public MemberTeamDto(Long memberId,String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.teamId = teamId;
        this.age = age;
        this.teamName = teamName;
    }
}
