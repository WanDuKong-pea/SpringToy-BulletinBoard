package toy.bullletinboard.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import toy.bullletinboard.web.interceptor.LogInterceptor;
import toy.bullletinboard.web.interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer { //implements WebMvcConfigurer: 인터셉터 등록을 위함


    @Override //WebMvcConfigurer 의 인터셉터 등록 메서드
    public void addInterceptors(InterceptorRegistry registry){
        //Log 인터셉터 등록
        registry.addInterceptor(new LogInterceptor())
                .order(1) //인터셉터 호출 순서. 낮을 수록 먼저 호출
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**","/*.ico","/boards/error"); //인터셉터에서 제외할 패턴 지정

        //로그인 인증 체크 인테셉터 등록
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**","/*.ico","/error",
                        "/boards","/member/**","/boards/error");
    }
}