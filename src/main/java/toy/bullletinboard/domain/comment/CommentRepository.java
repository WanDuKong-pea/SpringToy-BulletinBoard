package toy.bullletinboard.domain.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import toy.bullletinboard.mapper.CommentMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final CommentMapper commentMapper;

    public int count(Long boardId){
        return commentMapper.countComment(boardId);
    }

    public boolean deleteAll(Long boardId) {
        return commentMapper.deleteAllComment(boardId);
    }

    public boolean delete(Long commentId, String commenter){
        Map map = new HashMap();
        map.put("commentId", commentId);
        map.put("commenter", commenter);
        log.info("[삭제 댓글] commentId={}, commenter={}",commentId,commenter);
        return commentMapper.delete(map);
    }

    public boolean update(Comment comment){
        log.info("[수정 댓글] Comment={}",comment);
        return commentMapper.update(comment);
    }

    public boolean insert(Comment comment){
        log.info("[입력 댓글] Comment={}",comment);
        return commentMapper.insertComment(comment);
    }

    public List<Comment> selectListComment(Long boardId){
        return commentMapper.selectAllComment(boardId);
    }

    public Comment select(Long commentId){
        return commentMapper.selectOne(commentId);
    }

    public List<Comment> selectListReply(Long boardId){
        return commentMapper.selectAllReply(boardId);
    }
}
