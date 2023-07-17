package toy.bullletinboard.domain.member;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class MemberSaveForm {

    private Long id;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{6,12}$")
    private String loginId;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{6,12}$")
    private String password;

    @NotBlank
    private String chkPassword;

    @NotBlank
    @Size(min = 2, max = 8)
    private String nickName;

    @Email
    @NotBlank
    private String email;

    public MemberSaveForm() {
    }

    public MemberSaveForm(Long id, String loginId, String password, String nickName, String email) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.nickName = nickName;
        this.email = email;
    }
}
