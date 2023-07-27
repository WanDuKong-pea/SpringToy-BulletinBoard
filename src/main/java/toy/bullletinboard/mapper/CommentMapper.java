package toy.bullletinboard.mapper;

import org.apache.ibatis.annotations.*;
import toy.bullletinboard.domain.board.Board;
import toy.bullletinboard.domain.comment.Comment;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    @Delete("DELETE FROM comment WHERE boardId = #{boardId}")
    boolean deleteAllComment(Long boardId);

    @Select("SELECT count(*) FROM comment WHERE boardId = #{boardId}")
    int countComment(Long boardId);

    @Delete("DELETE FROM comment WHERE commentId = #{commentId} AND commenter = #{commenter}")
    boolean delete(Map map);

    @Insert("INSERT INTO comment(boardId, pcommentId, comment, commenter, regDate) " +
            "VALUES(#{boardId}, #{pcommentId}, #{comment}, #{commenter}, now())")
    boolean insertComment(Comment comment);

    @Select("SELECT commentId, boardId, pcommentId, comment, commenter, regDate " +
            "FROM comment WHERE boardId = #{boardId} AND pcommentId IS NULL ORDER BY regDate ASC, commentId ASC")
    List<Comment> selectAllComment(Long boardId);

    @Select("SELECT * fROM comment WHERE commentId = #{commentId}")
    Comment selectOne(Long commentId);

    @Select("SELECT * FROM comment WHERE boardId = #{boardId} AND pcommentId IS NOT NULL")
    List<Comment> selectAllReply(Long boardId);
}
