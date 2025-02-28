package com.nayak.Notes_APP.controller;

import com.nayak.Notes_APP.entity.User;
import com.nayak.Notes_APP.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
@RequestMapping("/api")
public class HomeController {

    @Autowired
    UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        User existingUser = userService.findByEmail(user.getEmail());

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found. Please register first.");
        }

        if (!existingUser.isEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account is not verified. Please verify first.");
        }

        if (!userService.authenticate(user.getEmail(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }

        return ResponseEntity.ok("Login Successful.");
    }




    @PostMapping("/createUser")
    public ResponseEntity<String> createUserControl(@RequestBody User user, HttpServletRequest request) {
        String url = request.getRequestURL().toString(); // http:localhost:8080/api/createUser
        url = url.replace(request.getServletPath(), "");// http:localhost:8080
        //getServletPath() --> /api/createUser

        if (userService.checkByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists.");
        }

        User newUser = userService.createUser(user, url);
        if (newUser != null) {
            return ResponseEntity.ok("Registered Successfully. verification mail has been sent");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error.");
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("code") String code) {
        if (userService.checkByVerificationCode(code)) {
            return ResponseEntity.ok("<h2>Verified Successfully,You can close this window</h2><br>");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<h2>Invalid or Expired Verification Code,Try to sign-up again</h2>");
        }
    }

}
