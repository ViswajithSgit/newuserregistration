package com.newuser.registration.service;

import com.newuser.registration.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService  {

    User createUser(User user);

    Optional<User> findOne(Long id);
}
