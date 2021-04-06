package vn.vnpt.sso.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import vn.vnpt.sso.config.AppProperties;
import vn.vnpt.sso.exception.BadRequestException;
import vn.vnpt.sso.security.TokenProvider;
import vn.vnpt.sso.util.CookieUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static vn.vnpt.sso.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * Lớp này dùng để xử lý phần OAuth sau khi xác thực thành công
 * Cung cấp 1 token cho phía client
 * Thực hiện chuyển hướng khi xác thực thành công
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

//    @Autowired
//    private TokenProvider tokenProvider;
//
//    @Autowired
//    private AppProperties appProperties;
//
//    @Autowired
//    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
//
//    /**
//     * xử lý khi xác thực thành công thực hiện chuyển hướng
//     *
//     * @param request        request
//     * @param response       response
//     * @param authentication Biến đại diện cho authenticated principal đã được xác thực trước đó
//     * @throws IOException      I/O exception
//     * @throws ServletException ServletException
//     */
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        // url chuyển hướng
//        String targetUrl = determineTargetUrl(request, response, authentication);
//
//        if (response.isCommitted()) {
//            logger.debug("Phản hồi đã được cam kết tới url: " + targetUrl);
//            return;
//        }
//        super.clearAuthenticationAttributes(request);
//        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//    }
//
//
//    /**
//     * Tạo đường dẫn chuyển hướng + token
//     *
//     * @param request        request
//     * @param response       response
//     * @param authentication Biến đại diện cho authenticated principal đã được xác thực trước đó
//     * @return trả đường dẫn chuyển hướng + token
//     */
//    @Override
//    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
//                .map(Cookie::getValue);
//
//        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
//            throw new BadRequestException("Đường dẫn xác thực không đúng với đăng ký trước đó");
//        }
//        // Lấy url
//        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
//
//        // Lấy token
//        String token = tokenProvider.createToken(authentication);
//
//        return UriComponentsBuilder.fromUriString(targetUrl)
//                .queryParam("token", token)
//                .build().toUriString();
//    }
//
//    /**
//     * Kiểm tra uri có phải là 1 đường dẫn được uỷ quyền
//     *
//     * @param uri uri
//     * @return true/false
//     */
//    protected boolean isAuthorizedRedirectUri(String uri) {
//        URI clientRedirectUri = URI.create(uri);
//        return appProperties.getOauth2().getAuthorizedRedirectUris()
//                .stream()
//                .anyMatch(authorizedRedirectUri -> {
//                    URI authorizedURI = URI.create(authorizedRedirectUri);
//                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) // kiểm tra đường dẫn host có trùng
//                            && authorizedURI.getPort() == clientRedirectUri.getPort()) { // Kiểm tra port có trùng
//                        return true;
//                    }
//                    return false;
//                });
//    }

    private TokenProvider tokenProvider;

    private AppProperties appProperties;

    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;


    @Autowired
    OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, AppProperties appProperties,
                                       HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.tokenProvider = tokenProvider;
        this.appProperties = appProperties;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String token = tokenProvider.createToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}
