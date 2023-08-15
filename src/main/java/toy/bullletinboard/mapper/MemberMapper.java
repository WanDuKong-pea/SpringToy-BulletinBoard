package toy.bullletinboard.mapper;

import org.apache.ibatis.annotations.*;
import toy.bullletinboard.domain.board.Board;
import toy.bullletinboard.domain.member.Member;

import java.util.*;

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

    @Update("UPDATE member set password = #{password} WHERE loginId=#{loginId}")
    void updatePwd(Map map);
}
