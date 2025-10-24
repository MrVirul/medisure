package com.virul.medisure.service;

import com.virul.medisure.dto.RegisterRequest;
import com.virul.medisure.model.User;
import com.virul.medisure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Validate password confirmation
        if (request.getConfirmPassword() != null && 
            !request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());
        // New users should be policy holders by default
        user.setRole(User.UserRole.POLICY_HOLDER);

        return userRepository.save(user);
    }

    public User createEmployee(String fullName, String email, String password, String phone, User.UserRole role) {
        // Validate inputs
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        if (!isEmployeeRole(role)) {
            throw new IllegalArgumentException("Role is not eligible for employee accounts");
        }

        user.setRole(role);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        // Fetch all users - no relationships needed for display
        List<User> users = userRepository.findAll();
        // Force initialization of any lazy fields if needed
        users.forEach(user -> {
            // Access basic fields to ensure they're loaded
            user.getId();
            user.getFullName();
            user.getEmail();
            user.getRole();
        });
        return users;
    }

    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    public User updateUser(Long id, String fullName, String email, String phone, User.UserRole role) {
        // Validate inputs
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (!isEmployeeRole(role)) {
            throw new IllegalArgumentException("Role is not eligible for employee accounts");
        }
        
        User user = getUserById(id);

        if (!isEmployeeRole(user.getRole())) {
            throw new IllegalArgumentException("Only employee accounts can be managed by admin");
        }
        
        // Check if email is being changed and if it's already taken
        if (!user.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        boolean removingAdminRole = user.getRole() == User.UserRole.ADMIN && role != User.UserRole.ADMIN;
        if (removingAdminRole) {
            long totalAdmins = userRepository.countByRole(User.UserRole.ADMIN);
            if (totalAdmins <= 1) {
                throw new IllegalArgumentException("At least one ADMIN account must remain in the system");
            }
        }
        
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (!isEmployeeRole(user.getRole())) {
            throw new IllegalArgumentException("Only employee accounts can be managed by admin");
        }

        if (user.getRole() == User.UserRole.ADMIN) {
            long totalAdmins = userRepository.countByRole(User.UserRole.ADMIN);
            if (totalAdmins <= 1) {
                throw new IllegalArgumentException("Cannot delete the last ADMIN account");
            }
        }
        
        userRepository.delete(user);
    }

    public void changeUserRole(Long userId, User.UserRole newRole) {
        User user = getUserById(userId);
        user.setRole(newRole);
        userRepository.save(user);
    }
    
    public List<User> searchUsers(String query, User.UserRole role) {
        if (role != null) {
            return userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .filter(u -> query == null || query.isEmpty() || 
                    u.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                    u.getEmail().toLowerCase().contains(query.toLowerCase()))
                .toList();
        }
        
        if (query == null || query.isEmpty()) {
            return userRepository.findAll();
        }
        
        return userRepository.findAll().stream()
            .filter(u -> u.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                        u.getEmail().toLowerCase().contains(query.toLowerCase()))
            .toList();
    }

    private boolean isEmployeeRole(User.UserRole role) {
        return role != null &&
            role != User.UserRole.POLICY_HOLDER &&
            role != User.UserRole.USER;
    }
}
