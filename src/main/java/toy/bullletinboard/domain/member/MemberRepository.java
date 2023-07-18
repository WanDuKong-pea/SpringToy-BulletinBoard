package toy.bullletinboard.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberRepository {
    private final Map<Long, Member> store = new HashMap<>();
    private long sequence = 0L;

    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        log.info("[가입한 회원] member={}",member);
        return member;
    }


    /**
     * db 자동 등록 id로 member 조회
     */
    public Member findById(Long id) {
        return store.get(id);
    }

    /**
     * db에 저장되는 자동 등록 id가 아닌 login 아이디로 member조회
     */
    public Optional<Member> findByLoginId(String loginId) {
        // Optional<T>는 null이 올 수 있는 값을 감싸는 Wrapper 클래스로,
        // 참조하더라도 NPE가 발생하지 않도록 도와줌
        //https://www.notion.so/8783dffde93f442a93a29e9b9e3a0b1d

        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }

    public Optional<Member> findByNickName(String nickName) {
        return findAll().stream()
                .filter(m -> m.getNickName().equals(nickName))
                .findFirst();
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
