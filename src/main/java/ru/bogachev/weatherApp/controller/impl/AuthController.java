package ru.bogachev.weatherApp.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bogachev.weatherApp.controller.AuthenticationApi;
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthenticationApi {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<SignUpResponse> signUp(
            @Valid final SignUpRequest request) {
        SignUpResponse response = authenticationService.singUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<JwtResponse> signIn(
            @Valid final SignInRequest request) {
        JwtResponse response = authenticationService.signIn(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccessJwtResponse> refreshAccessToken(
            @Valid final RefreshJwtRequest request) {
        AccessJwtResponse response = authenticationService
                .getAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<JwtResponse> refreshTokens(
            @Valid final RefreshJwtRequest request) {
        JwtResponse response = authenticationService.getRefreshTokens(request);
        return ResponseEntity.ok(response);
    }

}
