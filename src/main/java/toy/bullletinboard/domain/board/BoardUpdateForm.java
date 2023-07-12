package toy.bullletinboard.domain.board;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
    수정용 Board 객체 DTO와 같은 역할
 */
@Data
public class BoardUpdateForm {
    @NotBlank
    private String title;

    @NotBlank
    private String body;

    private String imgName;
}
