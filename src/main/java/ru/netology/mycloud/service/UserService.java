package ru.netology.mycloud.service;

import ru.netology.mycloud.model.User;

public interface UserService {
    User findUserByLogin(String username);
}
