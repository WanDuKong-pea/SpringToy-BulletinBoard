package toy.bullletinboard.domain.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    /**
     * 로그인 id 존재 유무
     */
    public boolean isLoginId(String loginId){
       return memberRepository.findByLoginId(loginId) == null;
    }

    /**
     * @return null이면 로그인 실패
     */
    public Member login(String loginId, String password){
        if(memberRepository.findByLoginId(loginId).getPassword().equals(encryptionPass(password))){
            return memberRepository.findByLoginId(loginId);
        }else {
            return null;
        }
    }

    public Member searchByLoginId(String id){
        return memberRepository.findByLoginId(id);
    }

    public Member searchByNickName(String nickname){
        return memberRepository.findByNickName(nickname);
    }

    public Member saveMember(Member member){
        member.setPassword(encryptionPass(member.getPassword()));
        return memberRepository.save(member);
    }

    public String encryptionPass(String password) {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 암호화 에러 발생:"+e);
        }
        md.update(password.getBytes());
        Base64 base= new Base64();
        return new String(base.encode(md.digest()));
    }
}
