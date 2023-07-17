package toy.bullletinboard.domain.board;

import lombok.Data;

@Data
public class Board {

    private Long boardId;
    private String memberId;
    private String title;
    private String body;
    private UploadFile imgName; //= new UploadFile("default","default"); //DB에 넣을 초기값
    private String regDate;
    private int views;

}
