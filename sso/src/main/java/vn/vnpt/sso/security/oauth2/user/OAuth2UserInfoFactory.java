package vn.vnpt.sso.security.oauth2.user;

import vn.vnpt.sso.entity.AuthProvider;
import vn.vnpt.sso.exception.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        // Trường hợp user đăng nhập bằng google
        if (registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        // Trường hợp user đăng nhập bằng facebook
        } else if (registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        // Trường hợp user đăng nhập bằng github
        } else if (registrationId.equalsIgnoreCase(AuthProvider.github.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        // Trường hợp user đăng nhập bằng url khác
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}


