package org.example.service;

import org.example.dto.CreateUserDto;
import org.example.dto.UpdateUserDto;
import org.example.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(CreateUserDto createUserDto);
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UpdateUserDto updateUserDto);
    void deleteUser(Long id);
    List<UserDto> getAllUsers();
}
