package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;

@Schema(description = "DTO для обновления данных пользователя")
public class UpdateUserDto {
    @Schema(description = "Имя пользователя", example = "Иван Петров")
    private String name;

    @Schema(description = "Email пользователя", example = "ivan.petrov@example.com")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Возраст пользователя", example = "30")
    @Min(value = 0, message = "Age must be positive")
    private Integer age;

    public UpdateUserDto() {
    }

    public UpdateUserDto(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UpdateUserDto{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
