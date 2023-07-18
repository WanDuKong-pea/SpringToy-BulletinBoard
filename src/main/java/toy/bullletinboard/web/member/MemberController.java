package toy.bullletinboard.web.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import toy.bullletinboard.domain.member.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("member") MemberLoginForm memberForm) {
        return "views/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("member") MemberLoginForm memberForm,
                        BindingResult bindingResult, HttpServletRequest request) {
        //필수 입력 부분
        if (bindingResult.hasErrors()) {
            log.info("loginForm errors={}", bindingResult);
            return "views/login";
        }

        //아이디 존재 유무
        if (loginService.isLoginId(memberForm.getLoginId())) {
            bindingResult.rejectValue("loginId", "loginFail");
            log.info("loginId error {}", bindingResult);
            return "views/login";
        }

        //비밀번호 부분
        Member loginMember = loginService.login(memberForm.getLoginId(), memberForm.getPassword());
        if (loginMember == null) {
            log.info("loginPass error {}", bindingResult);
            bindingResult.rejectValue("password", "loginFail");
            return "views/login";
        }

        //로그인 성공
        log.info("[로그인 유저] member={}", loginMember);

        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성 (기본 true 옵션)
        HttpSession session = request.getSession();
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        return "redirect:/boards";
    }

    /**
     * HttpSession을 이용한 로그아웃 처리
     */
    @GetMapping("/logout")
    public String logOutV3(HttpServletRequest request) {
        //세션을 만들어 반환하지 않도록 false 옵션 사용
        HttpSession session = request.getSession(false);

        //세션 삭제
        if (session != null) {
            session.invalidate();
        }

        log.info("[로그아웃]");
        return "redirect:/boards";
    }


    @GetMapping("/sign-in")
    public String siginIn(Model model) {
        model.addAttribute("member", new MemberSaveForm());
        return "views/sign-in";
    }

    /**
     * 아이디 중복검사
     */
    @ResponseBody
    @PostMapping("/duplicate-id")
    public boolean checkDuplicateId(@RequestParam("loginId") String loginId) {
        //.isPresent: Optional<Member> 객체가 값을 가지고 있는지 아닌지 확인 (값존재:true, 값없음:false)
        return !memberRepository.findByLoginId(loginId).isPresent();
    }

    /**
     * 닉네임 중복검사
     */
    @ResponseBody
    @PostMapping("/duplicate-nickname")
    public boolean checkDuplicateNickName(@RequestParam("nickName") String nickname) {
        return !memberRepository.findByNickName(nickname).isPresent();
    }

    @PostMapping("/sign-in")
    public String save(@Valid @ModelAttribute("member") MemberSaveForm memberForm,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //아이디 중복 필드 에러 (자바스크립트로 했지만 한번 더 검증)
        if (memberRepository.findByLoginId(memberForm.getLoginId()).isPresent()) {
            bindingResult.rejectValue("loginId", "duplicationId");
        }

        //닉네임 중복 필드 에러 (자바스크립트로 했지만 한번 더 검증)
        if (memberRepository.findByNickName(memberForm.getNickName()).isPresent()) {
            bindingResult.rejectValue("nickName", "duplicationNickName");
        }

        //비밀번호 확인 필드 에러
        if (!Objects.equals(memberForm.getPassword(), memberForm.getChkPassword())) {
            bindingResult.rejectValue("chkPassword", "chkPasswordError");
        }

        if (bindingResult.hasErrors()) {
            log.info("sign-in errors={}", bindingResult);
            return "/views/sign-in";
        }

        Member member = new Member();
        member.setLoginId(memberForm.getLoginId());
        member.setPassword(memberForm.getPassword());
        member.setEmail(memberForm.getEmail());
        member.setNickName(memberForm.getNickName());
        log.info("[가입 회원 데이터] member={}",member);

        Member savedMember = memberRepository.save(member);

        redirectAttributes.addAttribute("status", "true");

        return "redirect:/member/login";
    }
}
