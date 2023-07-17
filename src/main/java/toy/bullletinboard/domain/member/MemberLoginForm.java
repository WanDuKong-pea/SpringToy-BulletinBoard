package toy.bullletinboard.domain.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MemberLoginForm {
    @NotBlank
    private String loginId;

    @NotBlank
    private String password;
}
