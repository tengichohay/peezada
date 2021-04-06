package vn.vnpt.sso.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpt.sso.entity.User;
import vn.vnpt.sso.exception.ResourceNotFoundException;
import vn.vnpt.sso.repository.UserRepository;

/**
 * Lớp user service
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    /**
     * Load thông tin user từ db
     * @param email email người dùng
     * @return UserPrincipal
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));
        return UserPrincipal.create(user);
    }

    /**
     * Lấy thông tin user
     * @param id id user
     * @return User đã được xác thực
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
        return UserPrincipal.create(user);
    }


}
