package ru.bogachev.weatherApp.controller.api.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bogachev.weatherApp.controller.api.AuthenticationApiDocs;
import ru.bogachev.weatherApp.controller.api.JwtTokenApiDocs;
import ru.bogachev.weatherApp.dto.auth.*;
import ru.bogachev.weatherApp.service.AuthenticationService;
import ru.bogachev.weatherApp.service.JwtTokenService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController implements
        AuthenticationApiDocs, JwtTokenApiDocs {

    private final AuthenticationService authenticationService;
    private final JwtTokenService jwtTokenService;

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
        AccessJwtResponse response = jwtTokenService
                .generateAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<JwtResponse> refreshTokens(
            @Valid final RefreshJwtRequest request) {
        JwtResponse response = jwtTokenService
                .generateAccessAndRefreshTokens(request);
        return ResponseEntity.ok(response);
    }

}
