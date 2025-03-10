package ru.bogachev.weatherApp.service;

import ru.bogachev.weatherApp.dto.auth.AccessJwtResponse;
import ru.bogachev.weatherApp.dto.auth.JwtResponse;
import ru.bogachev.weatherApp.dto.auth.RefreshJwtRequest;

public interface JwtTokenService {

    AccessJwtResponse generateAccessToken(RefreshJwtRequest request);

    JwtResponse generateAccessAndRefreshTokens(RefreshJwtRequest request);

}
