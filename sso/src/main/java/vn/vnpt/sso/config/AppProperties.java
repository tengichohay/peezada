package vn.vnpt.sso.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMsec;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

    }

}
