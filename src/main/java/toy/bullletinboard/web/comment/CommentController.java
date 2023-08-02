package toy.bullletinboard.web.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @PutMapping()
    public ResponseEntity<Map<String, List<Comment>>> writeComment(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                                                   @RequestParam("boardId") Long boardId,
                                                                   @RequestParam("comment") String comment,
                                                                   @RequestParam(value = "pcommentId", required = false) Long pcommentId) {
        // 댓글을 입력하고 List<Comment> 형태로 결과를 반환
        //ResponseEntity는 제네릭을 사용하여 응답 본문의 데이터 타입을 지정할 수 있음
        log.info("[PutMapping 댓글] boardId={} comment={}",boardId,comment);

        Comment c = new Comment();
        c.setBoardId(boardId);
        c.setComment(comment);
        c.setCommenter(loginMember.getLoginId());

        if(pcommentId != null){
            c.setPcommentId(pcommentId);
        }

        //댓글 저장
        commentService.write(c);
        //댓글 리스트 반환
        return ResponseEntity.ok(getCommentList(boardId));
    }

    @DeleteMapping()
    public ResponseEntity<Map<String, List<Comment>>> deleteComment(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                                                    @RequestParam("commentId") Long commentId,
                                                                    @RequestParam("boardId") Long boardId){
        log.info("[DeleteMapping 댓글] commentId={} writer={}",commentId,loginMember.getLoginId());

        //댓글 삭제
        commentService.remove(commentId,loginMember.getLoginId());
        //댓글 리스트 반환
        return ResponseEntity.ok(getCommentList(boardId));
    }

    @PatchMapping()
    public ResponseEntity<Map<String, List<Comment>>> editComment(@SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                                                          @RequestParam("commentId") Long commentId,
                                                                          @RequestParam("comment")String comment,
                                                                          @RequestParam("boardId") Long boardId){
        log.info("[PatchMapping 댓글] commentId={} writer={}",commentId,loginMember.getLoginId());

        Comment c = new Comment();
        c.setCommentId(commentId);
        c.setComment(comment);
        c.setCommenter(loginMember.getLoginId());

        //댓글 수정
        commentService.edit(c);
        //댓글 리스트 반환
        return ResponseEntity.ok(getCommentList(boardId));
    }

    /**
     * 댓글과 대댓글 리스트를 새로 가지고 오는 작업
     */
    public Map<String, List<Comment>> getCommentList(Long boardId){
        List<Comment> comments = commentService.getCommentList(boardId);
        List<Comment> replies = commentService.getReplyList(boardId);

        Map<String, List<Comment>> result = new HashMap<>();
        result.put("comments", comments);
        result.put("replies", replies);

        return result;

    }
}
