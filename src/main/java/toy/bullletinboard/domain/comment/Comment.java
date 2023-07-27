package toy.bullletinboard.domain.comment;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private Long commentId;
    private Long boardId;
    private Long pcommentId;
    private String comment;
    private String commenter;
    private Date regDate;

    public Comment() {
    }

    public Comment(Long boardId, Long pcommentId, String comment, String commenter) {
        this.boardId = boardId;
        this.pcommentId = pcommentId;
        this.comment = comment;
        this.commenter = commenter;
    }
}
