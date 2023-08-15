package toy.bullletinboard.domain.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import toy.bullletinboard.mapper.BoardMapper;
import toy.bullletinboard.mapper.MemberMapper;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final MemberMapper memberMapper;

    /**
     * 신규 멤버 저장
     */
    public Member save(Member member) {
        //새로운 멤버 정보 저장
        memberMapper.insertMember(member);
        //저장된 멤버 조회 반환
        return memberMapper.selectById(member.getId());
    }

    /**
     * db 자동 등록 id로 member 조회
     */
    public Member findById(Long id) {
        return memberMapper.selectById(id);
    }

    /**
     * db에 저장되는 자동 등록 id가 아닌 login 아이디로 member조회
     */
    public Member findByLoginId(String loginId) {
        return memberMapper.selectByLoginId(loginId);
    }

    /**
     * db에 저장되는 닉네임으로 멤버 찾기
     */
    public Member findByNickName(String nickName) {
        return memberMapper.selectByNickName(nickName);
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findAll() {
        return memberMapper.selectAll();
    }

    /**
     * 비밀번호 업데이트
     */
    public void updatePwd(Map map){
        memberMapper.updatePwd(map);
    }
}
