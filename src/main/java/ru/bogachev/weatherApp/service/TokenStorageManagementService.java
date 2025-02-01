package ru.bogachev.weatherApp.service;

public interface TokenStorageManagementService {

    void save(Long userId, String token);

    String get(Long userId);

    void delete(Long userId);
}
