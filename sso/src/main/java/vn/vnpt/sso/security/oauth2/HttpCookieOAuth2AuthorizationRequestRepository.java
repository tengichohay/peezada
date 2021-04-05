package vn.vnpt.sso.security.oauth2;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import vn.vnpt.sso.util.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    /**
     * token khi user nhận được khi đăng nhập
     */
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    /**
     * Đường dẫn url xác thực
     */
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

    /**
     * Thời hạn của cookie
     */
    public static final int cookieExpireSeconds = 180;

    /**
     * Hàm load thông tin xác thực từ request
     *
     * @param httpServletRequest request yêu cầu xác thực
     * @return trả về thông tin cookies xác thực
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpServletRequest) {
        return CookieUtils.getCookie(httpServletRequest, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    /**
     * Lưu thông tin khi có yêu cầu xác thực
     *
     * @param authorizationRequest yêu cầu uỷ quyền
     * @param request              request
     * @param response             response
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        // Xoá cookie khi ko có yêu cầu uỷ quyền
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            return;
        }
        // Tạo cookie khi có yêu cầu uỷ quyền
        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest), cookieExpireSeconds);
        // Nhận redirectUri từ request
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        // Trường hợp người dùng chấp nhận yêu cầu xác thực bên thứ 3
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds
            );
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest httpServletRequest) {
        return this.loadAuthorizationRequest(httpServletRequest);
    }

    /**
     * Xoá bỏ cookie
     * @param request request
     * @param response response
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }
}
