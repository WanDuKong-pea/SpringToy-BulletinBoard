package toy.bullletinboard.domain.board;

import lombok.Data;

@Data
public class Board {

    private Long boardId;
    private String memberId;
    private String title;
    private String body;
    private String imgName;
    private String regDate;
    private int views;

}
