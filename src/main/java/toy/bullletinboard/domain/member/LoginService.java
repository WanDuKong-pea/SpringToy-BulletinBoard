package toy.bullletinboard.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    /**
     * 로그인 id 존재 유무
     */
    public boolean isLoginId(String loginId){
       return !memberRepository.findByLoginId(loginId).isPresent();

    }

    /**
     * @return null이면 로그인 실패
     */
    public Member login(String loginId, String password){
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
