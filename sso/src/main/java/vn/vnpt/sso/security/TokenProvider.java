package vn.vnpt.sso.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.vnpt.sso.config.AppProperties;


/**
 * Lớp này để tạo token
 */
@Service
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Autowired
    private AppProperties properties;

    /**
     * Hàm tạo token
     * @param authentication
     * @return 1 token
     */
    public String createToken(Authentication authentication){


        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();


        return null;
    }
}
