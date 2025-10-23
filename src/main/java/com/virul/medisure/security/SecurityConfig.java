package com.virul.medisure.security;

import com.virul.medisure.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Public resources - Thymeleaf templates
                .requestMatchers("/", "/home", "/index", "/auth/**", "/css/**", "/js/**", "/error/**", "/favicon.ico", "/static/**").permitAll()
                // Public API endpoints
                .requestMatchers("/api/auth/**", "/h2-console/**", "/uploads/**").permitAll()
                .requestMatchers("/api/policies/all", "/api/policies/active", "/api/stats").permitAll()
                // Admin pages and APIs
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                // Role-specific pages
                .requestMatchers("/policyholder/**").hasAnyRole("ADMIN", "POLICY_HOLDER", "USER")
                .requestMatchers("/doctor/**").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/agent/**").hasAnyRole("ADMIN", "SALES_OFFICER")
                // Role-specific APIs
                .requestMatchers("/api/operation-manager/**").hasAnyRole("ADMIN", "OPERATION_MANAGER")
                .requestMatchers("/api/policy-manager/**").hasAnyRole("ADMIN", "POLICY_MANAGER")
                .requestMatchers("/api/claims-manager/**").hasAnyRole("ADMIN", "CLAIMS_MANAGER")
                .requestMatchers("/api/finance-manager/**").hasAnyRole("ADMIN", "FINANCE_MANAGER")
                .requestMatchers("/api/sales-officer/**").hasAnyRole("ADMIN", "SALES_OFFICER")
                .requestMatchers("/api/customer-support/**").hasAnyRole("ADMIN", "CUSTOMER_SUPPORT_OFFICER")
                .requestMatchers("/api/medical-coordinator/**").hasAnyRole("ADMIN", "MEDICAL_COORDINATOR")
                .requestMatchers("/api/doctor/**").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/api/policy-holder/**").hasAnyRole("ADMIN", "POLICY_HOLDER", "USER")
                .requestMatchers("/api/tickets/**").authenticated()
                .anyRequest().authenticated()
            )
            // Form login for Thymeleaf pages
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Session management - use sessions for web, stateless for API
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            // Apply JWT filter only to API endpoints
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            // CSRF protection - enabled by default for form login
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/h2-console/**")
            )
            .authenticationProvider(authenticationProvider())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
