package toy.bullletinboard.domain.board;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Board {

    private Long boardId;
    private String memberId;
    private String title;
    private String body;
    private String imgName;
    private String regDate;
    private int views;

    public Board() {
    }

    public Board(String memberId, String title, String body, String imgName, int views) {
    this.memberId = memberId;
    this.title = title;
    this.body = body;
    this.imgName = imgName;
    this.views = views;
    }

}
