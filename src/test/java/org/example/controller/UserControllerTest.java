package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CreateUserDto;
import org.example.dto.UpdateUserDto;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUser() throws Exception {
        CreateUserDto createDto = new CreateUserDto("John Doe", "john@example.com", 30);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnValidationErrorWhenCreatingUserWithInvalidData() throws Exception {
        CreateUserDto invalidDto = new CreateUserDto("", "invalid-email", -5);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldGetUserById() throws Exception {
        User user = new User("Jane Doe", "jane@example.com", 25);
        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        userRepository.save(new User("User 1", "user1@example.com", 20));
        userRepository.save(new User("User 2", "user2@example.com", 30));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = userRepository.save(new User("Old Name", "old@example.com", 20));

        UpdateUserDto updateDto = new UpdateUserDto("New Name", "new@example.com", 25);

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void shouldPartiallyUpdateUser() throws Exception {
        User user = userRepository.save(new User("Old Name", "old@example.com", 20));

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setName("Partially Updated Name");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Partially Updated Name"))
                .andExpect(jsonPath("$.email").value("old@example.com"))
                .andExpect(jsonPath("$.age").value(20));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        User user = userRepository.save(new User("To Delete", "delete@example.com", 30));

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorWhenCreatingUserWithDuplicateEmail() throws Exception {
        userRepository.save(new User("Existing User", "existing@example.com", 25));

        CreateUserDto duplicateDto = new CreateUserDto("New User", "existing@example.com", 30);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
