package vn.vnpt.sso.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import vn.vnpt.sso.util.CookieUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static vn.vnpt.sso.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * Lớp này xử phần xác thực không thành công hoặc bị huỷ bỏ
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    /**
     * Hàm này xử phần đăng nhập thất bại chuyển hướng người dùng về trang chủ
     *
     * @param request   yêu cầu thất bại
     * @param response  phản hồi yêu cầu thất bại
     * @param exception exception
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("/");
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);


    }
}
