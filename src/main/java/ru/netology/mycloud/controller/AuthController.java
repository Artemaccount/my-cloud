package ru.netology.mycloud.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.netology.mycloud.dto.AuthRequestDTO;
import ru.netology.mycloud.model.User;
import ru.netology.mycloud.security.jwt.JwtTokenProvider;
import ru.netology.mycloud.service.UserService;

import java.util.HashMap;
import java.util.Map;


@RestController
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/test")
    public ResponseEntity test(@RequestHeader("auth-token") String token) {
        System.out.println(token);
        String tokenWithoutBearer = token.substring(7);
        if (jwtTokenProvider.validateToken(tokenWithoutBearer)) {
            return ResponseEntity.status(200).body("Success blyat");
        } else {
            return ResponseEntity.status(300).body("No blyat");
        }
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequestDTO requestDto) {
        try {
            String login = requestDto.getLogin();
            String token = jwtTokenProvider.createToken(login);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, requestDto.getPassword()));
            User user = userService.findUserByLogin(login);

            if (user == null) {
                throw new UsernameNotFoundException("User with login: " + login + " not found");
            }

            Map<Object, Object> response = new HashMap<>();
            response.put("auth-token", token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid login or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestHeader("auth-token") String token) {
        jwtTokenProvider.deleteToken(token);
        return ResponseEntity.ok("Success logout");
    }
}
