package com.ibm.fscc.loginservice.controller;

import com.ibm.fscc.loginservice.dto.*;
import com.ibm.fscc.loginservice.services.LoginService;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping(path = "/api/login")
public class LoginController {

    private final Environment env;
    private final LoginService loginService;

    public LoginController(Environment env, LoginService loginService) {
        this.env = env;
        this.loginService = loginService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticateUser(@Valid @RequestBody LoginDto loginDto) throws AuthenticationException {
        return ResponseEntity.ok(
                loginService.authenticateUser(loginDto.getEmail(), loginDto.getPassword()));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody EmailDto request) {
        loginService.requestPasswordReset(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/set-password")
    public ResponseEntity<Void> setPassword(@Valid @RequestBody PasswordUpdateDto request) {
        loginService.updatePasswordWithResetToken(request.getToken(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/status/check")
    public String status() {
        return "Working on port " + Objects.requireNonNull(env.getProperty("server.port")) + "!";
    }
}
