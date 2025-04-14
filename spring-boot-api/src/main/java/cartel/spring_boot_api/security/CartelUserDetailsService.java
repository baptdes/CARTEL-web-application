package cartel.spring_boot_api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service for loading user details for authentication.
 * It checks the username and password against the configured admin credentials.
 */
@Service
public class CartelUserDetailsService implements UserDetailsService {

    @Value("${admin.username:admin}")
    private String adminUsername;

    // Take from application.properties (default to BCrypt encoded "password" if not set)
    @Value("${admin.password:$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG}")
    private String adminPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (adminUsername.equals(username)) {
            return new User(adminUsername, adminPassword, new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
