package toy.bullletinboard.domain.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.bullletinboard.domain.board.BoardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public int getCount(Long boardId){
        return commentRepository.count(boardId);
    }

    public boolean remove(Long commentId, String commenter){
        return commentRepository.delete(commentId, commenter);
    }

    public boolean write(Comment comment){
        return commentRepository.insert(comment);
    }

    public List<Comment> getCommentList(Long boardId){
        return commentRepository.selectListComment(boardId);
    }

    public List<Comment> getReplyList(Long boardId){
        return commentRepository.selectListReply(boardId);
    }

    public Comment read(Long commentId){
        return commentRepository.select(commentId);
    }
}
