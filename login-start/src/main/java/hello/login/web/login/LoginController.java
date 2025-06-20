package hello.login.web.login;


import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginform") LoginForm form) {
        return "login/loginForm";
    }


    /**
     *  쿠키 - 세션쿠키 (브라우저 종료 시 그대로 종료), 영속 쿠키
     * @param form
     * @param bindingResult
     * @return
     */
    @PostMapping("/login")
    public String login(LoginForm form, BindingResult bindingResult, HttpServletResponse response) {


        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        if(loginMember == null) {
            bindingResult.reject("loginFailed", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리 TODO

        // 쿠키에 시간 정보를 담아서 주지 않는다면? 세션 쿠키 가 됨.
        Cookie cookie = new Cookie("memberId",
        String.valueOf(loginMember.getId()));

        response.addCookie(cookie);
        return "redirect:/";

    }


    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response,"memberId");
        return "redirect:/";
    }

    public void expireCookie(HttpServletResponse response, String cookieName) {
        // memberId 쿠키 이름 초기화 + 만료시간 0 으로 설정 함.
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }



}
