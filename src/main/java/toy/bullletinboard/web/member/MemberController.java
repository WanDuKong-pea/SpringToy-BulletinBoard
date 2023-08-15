package toy.bullletinboard.web.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import toy.bullletinboard.domain.comment.Comment;
import toy.bullletinboard.domain.member.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final LoginService loginService;
    private final JavaMailSender mailSender;

    /**
     * 로그인 페이지로 이동
     */
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("member") MemberLoginForm memberForm) {
        return "views/login";
    }

    /**
     * 로그인 요청 처리
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("member") MemberLoginForm memberForm,
                        BindingResult bindingResult, HttpServletRequest request){
        //필수 입력 부분
        if (bindingResult.hasErrors()) {
            log.info("loginForm errors={}", bindingResult);
            return "views/login";
        }

        //아이디 존재 유무
        if (!loginService.isLoginId(memberForm.getLoginId())) {
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

    /**
     * 회원가입 페이지로 이동
     */
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
        return loginService.searchByLoginId(loginId)==null;
    }

    /**
     * 닉네임 중복검사
     */
    @ResponseBody
    @PostMapping("/duplicate-nickname")
    public boolean checkDuplicateNickName(@RequestParam("nickName") String nickname) {
        return loginService.searchByNickName(nickname)==null;
    }

    /**
     * 회원 정보 저장 요청 처리
     */
    @PostMapping("/sign-in")
    public String saveMember(@Valid @ModelAttribute("member") MemberSaveForm memberForm,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes){

        //아이디 중복 필드 에러 (자바스크립트로 했지만 한번 더 검증)
        if (loginService.searchByLoginId(memberForm.getLoginId())!=null) {
            bindingResult.rejectValue("loginId", "duplicationId");
        }

        //닉네임 중복 필드 에러 (자바스크립트로 했지만 한번 더 검증)
        if (loginService.searchByLoginId(memberForm.getNickName())!=null) {
            bindingResult.rejectValue("nickName", "duplicationNickName");
        }

        //비밀번호 확인 필드 에러
        if (!Objects.equals(memberForm.getPassword(), memberForm.getChkPassword())) {
            bindingResult.rejectValue("chkPassword", "chkPasswordError");
        }

        if (bindingResult.hasErrors()) {
            log.info("sign-in errors={}", bindingResult);
            return "views/sign-in";
        }

        Member member = new Member();
        member.setLoginId(memberForm.getLoginId());
        member.setPassword(memberForm.getPassword());
        member.setEmail(memberForm.getEmail());
        member.setNickName(memberForm.getNickName());
        log.info("[가입 회원 데이터] member={}",member);

        loginService.saveMember(member);

        redirectAttributes.addAttribute("status", "true");

        return "redirect:/member/login";
    }

    /**
     * 임시 비밀번호 저장후 로그인 뷰 반환하는 메서드
     */
    @ResponseBody
    @GetMapping("/pwd-email")
    public ResponseEntity<Map<String, Boolean>> sendMail(
            @RequestParam("loginId") String loginId,
            @RequestParam("email") String email) {

        Map<String,Boolean> map = new HashMap<>();
        map.put("status",true);

        //아이디 존재 유무, 이메일 확인
        if (!loginService.isLoginId(loginId) || !loginService.searchByLoginId(loginId).getEmail().equals(email)){
           map.put("status",false);
            return ResponseEntity.ok(map);
        }

        String temporaryPassword = UUID.randomUUID().toString();
        loginService.editPwd(loginId,temporaryPassword);

        sendTemporaryPasswordEmail(email,temporaryPassword);

        return ResponseEntity.ok(map);
    }

    /**
     * 임시비밀번호 메일로 전송하는 메서드
     */
    private void sendTemporaryPasswordEmail(String recipientEmail, String temporaryPassword) {
        String subject = "임시 비밀번호";
        String message = "임시 비밀번호: " + temporaryPassword;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("sophisticateo_o@naver.com");

        mailSender.send(mailMessage);
    }
}
