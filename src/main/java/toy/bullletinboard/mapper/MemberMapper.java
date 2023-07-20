package toy.bullletinboard.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import toy.bullletinboard.domain.board.Board;
import toy.bullletinboard.domain.member.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    @Insert("INSERT INTO member (loginId, password, nickName, email) " +
            "VALUES (#{loginId}, #{password}, #{nickName}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertMember(Member member);

    @Select("SELECT * FROM member WHERE id=#{id}")
    Member selectById(Long id);

    @Select("SELECT * FROM member WHERE loginId=#{loginId}")
    Member selectByLoginId(String loginId);

    @Select("SELECT * FROM member WHERE nickName=#{nickName}")
    Member selectByNickName(String nickName);

    @Select("SELECT * FROM member")
    List<Member> selectAll();
}
