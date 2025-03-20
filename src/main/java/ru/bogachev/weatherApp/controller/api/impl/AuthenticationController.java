package ru.bogachev.weatherApp.controller.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.bogachev.weatherApp.controller.api.AuthenticationApiDocs;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.SignInRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpRequest;
import ru.bogachev.weatherApp.dto.auth.SignUpResponse;
import ru.bogachev.weatherApp.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationApiDocs {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<SignUpResponse> signUp(
            final SignUpRequest request) {
        SignUpResponse response = authenticationService.singUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<JwtResponse> signIn(
            final SignInRequest request) {
        JwtResponse response = authenticationService.signIn(request);
        return ResponseEntity.ok(response);
    }

}
