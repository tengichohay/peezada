package vn.vnpt.sso.security.oauth2;

import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Lớp này dùng để xử lý phần OAuth sau khi xác thực thành công
 * Cung cấp 1 token cho phía client
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private Token
}
