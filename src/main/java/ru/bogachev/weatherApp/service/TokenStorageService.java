package ru.bogachev.weatherApp.service;

public interface TokenStorageService {

    void save(Long userId, String token);

    String get(Long userId);

    void delete(Long userId);
}
