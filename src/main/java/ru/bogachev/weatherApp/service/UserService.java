package ru.bogachev.weatherApp.service;

import ru.bogachev.weatherApp.model.user.User;

public interface UserService {

    User create(User user);

    User getByEmail(String email);

}
