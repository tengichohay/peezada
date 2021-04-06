package vn.vnpt.sso.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vnpt.sso.entity.User;
import vn.vnpt.sso.exception.ResourceNotFoundException;
import vn.vnpt.sso.repository.UserRepository;
import vn.vnpt.sso.security.CurrentUser;
import vn.vnpt.sso.security.UserPrincipal;

@RestController
@CrossOrigin(value = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
