package ru.bogachev.weatherApp.service;

import ru.bogachev.weatherApp.dto.auth.*;

public interface AuthService {

    SignUpResponse singUp(SignUpRequest request);

    JwtResponse signIn(SignInRequest request);

    JwtResponse getAccessToken(RefreshJwtRequest request);

    JwtResponse refresh(RefreshJwtRequest request);

}
