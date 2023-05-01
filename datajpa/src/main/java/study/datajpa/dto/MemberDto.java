package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import study.datajpa.entity.Member;

@Data
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String teamName;
    private String userName;

    public MemberDto(Long id, String teamName, String userName) {
        this.id = id;
        this.teamName = teamName;
        this.userName = userName;
    }

    public MemberDto(Member member) {
        this.id = member.getMemberId();
        this.userName = member.getUserName();
    }
}
