package vn.vnpt.sso.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.vnpt.sso.entity.AuthProvider;
import vn.vnpt.sso.entity.User;
import vn.vnpt.sso.exception.OAuth2AuthenticationProcessingException;
import vn.vnpt.sso.repository.UserRepository;
import vn.vnpt.sso.security.UserPrincipal;
import vn.vnpt.sso.security.oauth2.user.OAuth2UserInfo;
import vn.vnpt.sso.security.oauth2.user.OAuth2UserInfoFactory;

import java.util.Optional;

/**
 * Lớp này thực hiện xử phần thông tin của user khi đăng nhập thành công
 * Load thông tin user, đăng ký khi user đăng ký
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Hàm load thông tin user khi đăng nhập
     *
     * @param userRequest thông tin user ở request gửi về
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Hàm xử lý đăng nhập của user khi đăng nhập
     *
     * @param request    request
     * @param oAuth2User thông tin user khi xác thực
     * @return UserPrincipal
     */
    public OAuth2User processOAuth2User(OAuth2UserRequest request, OAuth2User oAuth2User) {
        // Thông tin của user đang đăng nhập bằng social
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(request.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        // Trường hợp không tìm thấy email
        if (!StringUtils.hasText(oauth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        // Check tồn tại user hay chưa
        Optional<User> userOptional = userRepository.findByEmail(oauth2UserInfo.getEmail());

        User user;
        // Nếu user tồn tại
        if (userOptional.isPresent()) {
            // Lấy thông tin user
            user = userOptional.get();
            // Trường hợp user đã đăng ký
            if (!user.getProvider().equals(AuthProvider.valueOf(request.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            // cập nhật lại thông tin user
            user = updateExistingUser(user, oauth2UserInfo);
        } else { // Trường hợp chưa đăng ký thì tự động đăng ký cho user mới
            user = registerNewUser(request, oauth2UserInfo);
        }
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    /**
     * Lưu thông tin user vào db khi đăng nhập bằng social
     *
     * @param request        request
     * @param oauth2UserInfo thông tin user từ request khi xác thực thành công
     * @return lưu thông tin user vào db
     */
    public User registerNewUser(OAuth2UserRequest request, OAuth2UserInfo oauth2UserInfo) {
        User user = new User();
        user.setProvider(AuthProvider.valueOf(request.getClientRegistration().getRegistrationId()));
        user.setProviderId(oauth2UserInfo.getId());
        user.setName(oauth2UserInfo.getName());
        user.setEmail(oauth2UserInfo.getEmail());
        user.setImageUrl(oauth2UserInfo.getImageUrl());
        return userRepository.save(user);
    }

    /**
     * Cập nhật thông tin user
     *
     * @param existingUser   thông tin user đã lưu
     * @param oAuth2UserInfo thông tin user cần lưu
     * @return user cập nhật
     */
    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}
