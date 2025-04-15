package com.codemaster.switchadmin.service;

import com.codemaster.switchadmin.entity.Permission;
import com.codemaster.switchadmin.entity.Role;
import com.codemaster.switchadmin.entity.UserAccount;
import com.codemaster.switchadmin.repository.UserAccountRepository;
import com.codemaster.switchadmin.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private UserAccountRepository userAccountRepository;

    @Autowired
    public UserDetailsServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        UserAccount user = userAccountRepository.findByEmailWithRolesAndPermissions(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));


        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(
                        user.getRoles().stream()
                                .flatMap(role -> role.getPermissions().stream())
                                .map(Permission::getName)
                                .distinct()
                                .toArray(String[]::new)
                )
                .build();
    }
}
