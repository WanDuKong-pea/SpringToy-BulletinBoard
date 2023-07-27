package toy.bullletinboard.web.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.bullletinboard.domain.board.Board;
import toy.bullletinboard.domain.comment.Comment;
import toy.bullletinboard.domain.comment.CommentService;
import toy.bullletinboard.domain.member.Member;
import toy.bullletinboard.domain.member.SessionConst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 입력 및 재차로 뷰에 뿌리기
     */
    @GetMapping("/write")
    public ResponseEntity<Map<String, List<Comment>>> writeComment(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                                                  @RequestParam("boardId") Long boardId,
                                                                  @RequestParam("comment") String comment) {
        // 댓글을 입력하고 List<Comment> 형태로 결과를 반환
        //ResponseEntity는 제네릭을 사용하여 응답 본문의 데이터 타입을 지정할 수 있음
        log.info("[입력 댓글] boardId={} comment={}",boardId,comment);

        Comment c = new Comment();
        c.setBoardId(boardId);
        c.setComment(comment);
        c.setCommenter(loginMember.getLoginId());

        commentService.write(c);
        List<Comment> comments = commentService.getCommentList(boardId);
        List<Comment> replies = commentService.getReplyList(boardId);

        Map<String, List<Comment>> result = new HashMap<>();
        result.put("comments", comments);
        result.put("replies", replies);

        return ResponseEntity.ok(result);
        //00 OK 상태 코드와 함께 응답을 생성
    }
}
