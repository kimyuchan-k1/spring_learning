package hello.login.web.session;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    // 세션 저장소 역할을 할 것임. - 세션 관리까지
    public static final String SESSION_COOKIE_NAME =  "mySessionId";

    private Map<String,Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성
     */

    public void createSession(Object value, HttpServletResponse response) {

        // session Id -> UUID 로 생성.
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId,value);

        // 쿠키 생성
        Cookie mySessionCookie =  new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(mySessionCookie);

    }

    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request,SESSION_COOKIE_NAME);
        if(sessionCookie == null) {
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션 만료
     */

    public void expireSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue());
        }
    }

    // 내부 로직
    private Cookie findCookie(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(
                request.getCookies()
        ).filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }





}
