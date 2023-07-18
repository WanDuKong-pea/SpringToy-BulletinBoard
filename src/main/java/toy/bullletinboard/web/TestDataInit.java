package toy.bullletinboard.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import toy.bullletinboard.domain.board.BoardRepository;
import toy.bullletinboard.domain.member.Member;
import toy.bullletinboard.domain.member.MemberRepository;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final BoardRepository itemRepository;
    private final MemberRepository memberRepository;

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        //@PostConstruct: 의존성 주입이 이루어진 후 초기화를 수행하는 메서드.
        //클래스가 service()를 수행하기 전에 발생.

        //itemRepository.save(new Board("cok854", "테스트용 데이터1", "본문입니다.","default-image.png",0));
        //itemRepository.save(new Board("cok894", "테스트용 데이터2", "본문입니다.","default-image.png",0));

        Member member = new Member();
        member.setLoginId("test123");
        member.setPassword("ffff1234");
        member.setNickName("테스터");
        member.setEmail("cok@naver.com");
        memberRepository.save(member);
    }
}
