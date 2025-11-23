package org.example.service.impl;

import org.example.dto.CreateUserDto;
import org.example.dto.UpdateUserDto;
import org.example.dto.UserDto;
import org.example.dto.UserMapper;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new IllegalArgumentException("User with email " + createUserDto.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(createUserDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        // Check if email is being changed and if new email already exists
        if (updateUserDto.getEmail() != null &&
            !updateUserDto.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(updateUserDto.getEmail())) {
            throw new IllegalArgumentException("User with email " + updateUserDto.getEmail() + " already exists");
        }

        userMapper.updateEntity(user, updateUserDto);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
