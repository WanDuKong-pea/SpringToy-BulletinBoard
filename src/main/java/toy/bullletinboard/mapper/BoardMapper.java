package toy.bullletinboard.mapper;

import org.apache.ibatis.annotations.*;
import toy.bullletinboard.domain.board.Board;
import java.util.ArrayList;
import java.util.List;


@Mapper
public interface BoardMapper {
    @Select("SELECT*FROM board")
    ArrayList<Board> selectAllBoard();

    @Select("SELECT * FROM board WHERE boardId=#{id}")
    Board selectById(Long id);

    @Select("SELECT * FROM board WHERE title like CONCAT('%', #{searchData}, '%')")
    List<Board> selectBoardLikeTitle(String searchData);

    @Update("UPDATE board SET views = views + 1 WHERE boardId=#{id}")
    boolean updateViews(Long id);


    @Delete("DELETE FROM board WHERE boardId=#{id}")
    boolean deleteBoard(Long id);

    @Insert("INSERT INTO board (memberId, title, body, imgName, regDate, views ) " +
            "VALUES (#{memberId}, #{title}, #{body}, #{imgName}, #{regDate}, #{views} )")
    @Options(useGeneratedKeys = true, keyProperty = "boardId")
    void insertBoard(Board board);

    @Update("UPDATE board SET title = #{title}, body = #{body}, imgName = #{imgName} WHERE boardId = #{boardId}")
    @Options(useGeneratedKeys = true, keyProperty = "boardId")
    void updateBoard(Board board);

}
