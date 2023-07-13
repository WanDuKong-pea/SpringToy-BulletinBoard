package toy.bullletinboard.controller.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import toy.bullletinboard.domain.member.Member;
import toy.bullletinboard.domain.member.MemberRepository;
import toy.bullletinboard.domain.member.MemberSaveForm;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/login")
    public String login(){
        return "views/login";
    }

    @GetMapping("/sign-in")
    public String siginIn(Model model){
        model.addAttribute("member",new MemberSaveForm());
        return "views/sign-in";
    }

    @PostMapping("/sign-in")
    public String save(@Valid @ModelAttribute("member") MemberSaveForm memberForm,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //아이디 중복 필드 에러
        if(memberRepository.findByLoginId(memberForm.getLoginId()).isPresent()){
            //.isPresent: Optional<Member> 객체가 값을 가지고 있는지 아닌지 확인 (값존재:true, 값없음:false)
            bindingResult.rejectValue("loginId", "duplicationId");
        }

        //닉네임 중복 필드 에러
        if(memberRepository.findByNickName(memberForm.getNickName()).isPresent()){
            bindingResult.rejectValue("nickName", "duplicationNickName");
        }

        //비밀번호 확인 필드 에러
        if (!Objects.equals(memberForm.getPassword(), memberForm.getChkPassword())) {
            bindingResult.rejectValue("chkPassword", "chkPasswordError");
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "views/sign-in";
        }

        log.info("Controller memberForm={}", memberForm);

        Member member = new Member();
        member.setLoginId(memberForm.getLoginId());
        member.setPassword(memberForm.getPassword());
        member.setEmail(memberForm.getEmail());
        member.setNickName(memberForm.getNickName());

        memberRepository.save(member);

        redirectAttributes.addAttribute("status","true");

        return "redirect:/member/login";
    }
}
