package ru.bogachev.weatherApp.controller.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bogachev.weatherApp.controller.api.JwtTokenApiDocs;
import ru.bogachev.weatherApp.dto.auth.AccessJwtResponse;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.RefreshJwtRequest;
import ru.bogachev.weatherApp.service.JwtTokenService;

@RestController
@RequiredArgsConstructor
public class JwtTokenController implements JwtTokenApiDocs {

    private final JwtTokenService jwtTokenService;

    @Override
    public ResponseEntity<AccessJwtResponse> refreshAccessToken(
            final RefreshJwtRequest request) {
        AccessJwtResponse response = jwtTokenService
                .generateAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<JwtResponse> refreshTokens(
            final RefreshJwtRequest request) {
        JwtResponse response = jwtTokenService
                .generateAccessAndRefreshTokens(request);
        return ResponseEntity.ok(response);
    }
}
