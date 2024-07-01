package com.event_management.services;

import com.event_management.dto.ReqRes;
import com.event_management.entities.User;
import com.event_management.jwt.JwtUtils;
import com.event_management.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepo userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    // All Users
    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<User> result = userRepository.findAll();
            if(!result.isEmpty()) {
                reqRes.setUserList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User list found successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No user found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    // Get user by Id
    public ReqRes getUserById(Long id) {
        ReqRes reqRes = new ReqRes();
        try {
            User userById = userRepository.findById(id).
                    orElseThrow(() -> new UsernameNotFoundException("User with id: " + id + " not found"));
            reqRes.setUser(userById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("User exist with id: " + id);
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    // Register User
    public ReqRes createUser(ReqRes registrationRequest) {
        ReqRes res = new ReqRes();

        try {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setFirstName(registrationRequest.getFirstName());
            user.setLastName(registrationRequest.getLastName());
            user.setRole(registrationRequest.getRole());

            User newUser = userRepository.save(user);

            if(newUser.getId() > 0){
                res.setUser((newUser));
                res.setMessage("User saved successfully");
                res.setStatusCode(200);
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setError(e.getMessage());
        }
        return res;
    }

    // Login user
    public ReqRes login(ReqRes loginRequest){
        ReqRes response = new ReqRes();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail());
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24 Hours");
            response.setMessage("Successfully logged in");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    // Update user
    public ReqRes updateUser(Long id, User updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<User> user = userRepository.findById(id);
            if(user.isPresent()){
                User existingUser = user.get();
                existingUser.setFirstName(updatedUser.getFirstName());
                existingUser.setLastName(updatedUser.getLastName());
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setRole(updatedUser.getRole());

                if(updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()){
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                User savedUser = userRepository.save(existingUser);
                reqRes.setUser(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully with id: " + id);
            }
        } catch (Exception e){
            reqRes.setStatusCode(404);
            reqRes.setMessage("No user found");
        }
        return reqRes;
    }

    // Delete user
    public ReqRes deleteUser(Long id) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<User> user = userRepository.findById(id);
            if(user.isPresent()){
                userRepository.deleteById(id);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully with id: " + id);
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No user found");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        return user;
    }

    // My Profile
    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            User optionalUser = userRepository.findByEmail(email);
            if(optionalUser != null){
                reqRes.setUser(optionalUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No user found");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }
}
