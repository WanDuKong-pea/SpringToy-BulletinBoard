package toy.bullletinboard.domain.member;

import lombok.Data;

@Data
public class Member {
    private Long id;
    private String loginId;
    private String password;
    private String nickName;
    private String email;
}
