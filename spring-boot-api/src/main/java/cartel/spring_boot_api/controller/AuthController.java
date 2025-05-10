package cartel.spring_boot_api.controller;

import cartel.spring_boot_api.model.AuthRequest;
import cartel.spring_boot_api.model.AuthResponse;
import cartel.spring_boot_api.security.CartelUserDetailsService;
import cartel.spring_boot_api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CartelUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    // As there is no need to have accounts for each admin, we can use a single admin account
    // with no username, just a password
    @Value("${admin.username:admin}") //Take from application.properties (default to "admin" if not set)
    private String adminUsername;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(adminUsername, authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Mot de passe incorrect");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(adminUsername);
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
