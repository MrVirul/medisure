package com.virul.medisure.controller;

import com.virul.medisure.dto.LoginRequest;
import com.virul.medisure.dto.RegisterRequest;
import com.virul.medisure.dto.AuthResponse;
import com.virul.medisure.user.User;
import com.virul.medisure.user.PolicyHolder;
import com.virul.medisure.user.UserRole;
import com.virul.medisure.service.UserService;
import com.virul.medisure.security.JwtTokenProvider;
import com.virul.medisure.repository.PolicyHolderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PolicyHolderRepository policyHolderRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        return ResponseEntity.ok(new AuthResponse(jwt, refreshToken));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerPolicyHolder(@Valid @RequestBody RegisterRequest registerRequest) {
        // Check if username or email already exists
        if (policyHolderRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        
        if (policyHolderRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }
        
        if (policyHolderRepository.existsByPolicyNumber(registerRequest.getPolicyNumber())) {
            return ResponseEntity.badRequest().body("Policy number is already registered!");
        }
        
        // Create new policy holder
        PolicyHolder policyHolder = new PolicyHolder();
        policyHolder.setUsername(registerRequest.getUsername());
        policyHolder.setEmail(registerRequest.getEmail());
        policyHolder.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        policyHolder.setFirstName(registerRequest.getFirstName());
        policyHolder.setLastName(registerRequest.getLastName());
        policyHolder.setPhoneNumber(registerRequest.getPhoneNumber());
        policyHolder.setRole(UserRole.POLICY_HOLDER);
        policyHolder.setPolicyNumber(registerRequest.getPolicyNumber());
        // Set default values for policy holder specific fields
        policyHolder.setCoverageAmount(BigDecimal.ZERO);
        policyHolder.setPremiumAmount(BigDecimal.ZERO);
        
        PolicyHolder savedPolicyHolder = policyHolderRepository.save(policyHolder);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPolicyHolder);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = jwtTokenProvider.getUsernameFromJWT(refreshToken);
        
        // Create authentication object
        Optional<User> userOptional = userService.getUserByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // For simplicity, we're creating a basic authentication object
        // In a real application, you might want to load the full authentication object
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(username, null, null);
        
        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
    }
}