package ru.bogachev.weatherApp.service;

import ru.bogachev.weatherApp.dto.auth.*;

public interface AuthService {

    SignUpResponse singUp(SignUpRequest request);

    SignInResponse signIn(SignInRequest request);

    SignInResponse refreshTokens(RefreshRequest request);

}
