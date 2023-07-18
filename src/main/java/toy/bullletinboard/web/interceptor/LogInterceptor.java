package toy.bullletinboard.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    public static final String LOG_ID = "logId";


    @Override //preHandle: 컨트롤러 실행 전
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception{

        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        //서블릿 필터의 경우 지역변수로 해결이 가능하지만
        //스프링 인터셉터는 호출 시점이 완전 분리되어 있기 때문에
        //preHandle에서 지정한 값을 postHandle, afterCompletion에서
        //함께 사용하려면 어딘가에 담아 두어야 함
        request.setAttribute(LOG_ID,uuid);

        //정적 (/resources/static): ResourceHttpRequestHandler
        if(handler instanceof HandlerMethod){
            //헨들러 정보는 어떤 헨들러 매핑을 사용하는가에 따라 달라짐
            //스프링을 사용하면 일반적으로 @Controller, @RequestMapping을 활용한 헨들러 매핑 사용
            //이 경우 헨들러 정보로 HandlerMethod가 넘어옴
            HandlerMethod hm = (HandlerMethod) handler;
        }

        log.info("REQUEST [{}][{}][{}]",uuid, requestURI, handler);

        //true면 정상 호출. 다음 인터셉터나 컨트롤러가 호출됨
        return true; //false 진행 X,
    }

    @Override //postHandle: 컨트롤러 실행 후
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception{
        log.info("postHandle [{}]",modelAndView);
    }

    @Override //afterCompletion: 요청 완료 후(View 반환 이후)
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception{
        //종료 로그를 postHandle이 아닌 afterCompletioin에서 실행한 이유
        //-> 에외가 발생한 경우 postHandle은 호출되지 않기 때문
        //afterCompletion은 예외가 발생해도 호출되는 것을 보장

        String requestURI = request.getRequestURI();
        String logId = (String)request.getAttribute(LOG_ID);

        //interceptor에서 request.getDispatcherType() log로 에러 정보 남기기
        log.info("RESPONSE [{}][{}][{}]", logId, request.getDispatcherType() ,requestURI);
    }
}
