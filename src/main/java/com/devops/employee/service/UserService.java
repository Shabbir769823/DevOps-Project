package com.devops.employee.service;

import com.devops.employee.model.User;
import java.util.List;

public interface UserService {
    User registerUser(User user);
    User findByUsername(String username);
    User updateProfile(String username, String firstName, String lastName, String email);
    void changePassword(String username, String oldPassword, String newPassword);
    List<User> getAllUsers();
    User getFirstAdmin();
    User findById(Long id);
}

