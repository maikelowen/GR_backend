package com.projects.casinApp_v3.controller;

import com.projects.casinApp_v3.dto.JwtResponse;
import com.projects.casinApp_v3.dto.SignInRequest;
import com.projects.casinApp_v3.dto.SignUpRequest;
import com.projects.casinApp_v3.model.ERole;
import com.projects.casinApp_v3.model.Role;
import com.projects.casinApp_v3.model.Session;
import com.projects.casinApp_v3.model.User;
import com.projects.casinApp_v3.repository.GRWalletRepository;
import com.projects.casinApp_v3.repository.RoleRepository;
import com.projects.casinApp_v3.repository.SessionRepository;
import com.projects.casinApp_v3.repository.UserRepository;
import com.projects.casinApp_v3.service.UserDetailsImpl;
import com.projects.casinApp_v3.service.UserDetailsServiceImpl;
import com.projects.casinApp_v3.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private SessionRepository sessionRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    private UserDetailsServiceImpl userDetailsService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository,
                          SessionRepository sessionRepository,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.sessionRepository = sessionRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Post mapping for the sigin request
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignInRequest signInRequest) {
        //Authenticate the user
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        //Setting the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //Generate JWT token
        String jwt = jwtUtil.generateJwtToken(authentication);
        //Get UserDetail object from Principal
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //Get list of roles from authorithies and convert them to String List
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        //Set response in JwtResponse DTO
        JwtResponse res = new JwtResponse();
        res.setToken(jwt);
        res.setId(userDetails.getId());
        res.setUsername(userDetails.getUsername());
        res.setRoles(roles);
        //Set session object and store it in DB
        Session session = new Session();
        session.setToken(jwt);
        session.setId(userDetails.getId());
        session.setUsername(userDetails.getUsername());
        LocalDate localDate = LocalDate.now();
        session.setDate(localDate);
        sessionRepository.save(session);
        return ResponseEntity.ok(session);
    }
    // Post mapping for the signup request
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequest signUpRequest) {
        //Create extId
        int extId = (int) (userRepository.count()+101);
        //Lookup by username and email
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("username is already taken");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email is already taken");
        }
        //Encode password
        String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        //Initialize roles list
        Set<Role> roles = new HashSet<>();
        //Set userRole from DB
        Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
        // If role does not exist in DB return
        if (userRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("role not found");
        }
        roles.add(userRole.get());
        //Create user object and save it on DB
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setRoles(roles);
        user.setExtId(extId);
        System.out.println(user);
        userRepository.save(user);
        return ResponseEntity.ok("User registered success");
    }
}
