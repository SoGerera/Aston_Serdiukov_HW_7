package org.example.dto;

import org.example.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }

    public User toEntity(CreateUserDto dto) {
        if (dto == null) {
            return null;
        }
        return new User(
                dto.getName(),
                dto.getEmail(),
                dto.getAge()
        );
    }

    public void updateEntity(User user, UpdateUserDto dto) {
        if (user == null || dto == null) {
            return;
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
    }
}
