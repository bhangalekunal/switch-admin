package com.codemaster.switchadmin.service;

import com.codemaster.switchadmin.dto.AuthRequest;
import com.codemaster.switchadmin.dto.AuthResponse;
import com.codemaster.switchadmin.entity.UserAccount;
import com.codemaster.switchadmin.repository.UserAccountRepository;
import com.codemaster.switchadmin.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(userDetails))
                .build();
    }
}
