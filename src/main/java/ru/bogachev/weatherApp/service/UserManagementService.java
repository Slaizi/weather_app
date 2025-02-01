package ru.bogachev.weatherApp.service;

import ru.bogachev.weatherApp.model.user.User;

public interface UserManagementService {

    void create(User user);

    User getByEmail(String email);

}
