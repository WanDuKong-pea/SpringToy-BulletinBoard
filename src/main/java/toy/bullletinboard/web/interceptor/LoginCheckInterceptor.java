package toy.bullletinboard.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import toy.bullletinboard.domain.member.SessionConst;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 로그인 인증 체크를 하는 인터셉터
 */
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    //인증 체크는 컨트롤러 호출 전에만 호출되면 됨 -> preHandle 만 필요
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        log.info("[로그인 인증 체크 인터셉터] requestURI={}",requestURI);
        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
            log.info("[미인증 사용자 요청]");
            response.sendRedirect("/boards?redirectURL=" + requestURI);
            return false;
        }

        return true;
    }
}