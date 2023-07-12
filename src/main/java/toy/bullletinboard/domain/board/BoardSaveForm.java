package toy.bullletinboard.domain.board;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
/**
    저장용 Board 객체 DTO와 같은 역할
*/
@Data
public class BoardSaveForm {

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    private String memberId;

    private String imgName;

}
