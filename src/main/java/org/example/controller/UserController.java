package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.CreateUserDto;
import org.example.dto.UpdateUserDto;
import org.example.dto.UserDto;
import org.example.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Управление пользователями", description = "API для управления пользователями системы")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(
        summary = "Создать нового пользователя",
        description = "Создает нового пользователя в системе с предоставленными данными"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно создан",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные входные данные",
            content = @Content
        )
    })
    public ResponseEntity<UserDto> createUser(
            @Parameter(description = "Данные для создания пользователя", required = true)
            @Valid @RequestBody CreateUserDto createUserDto) {
        UserDto createdUser = userService.createUser(createUserDto);

        // Добавление HATEOAS ссылок
        createdUser.add(linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel());
        createdUser.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        createdUser.add(linkTo(methodOn(UserController.class).updateUser(createdUser.getId(), null)).withRel("update"));
        createdUser.add(linkTo(methodOn(UserController.class).deleteUser(createdUser.getId())).withRel("delete"));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Получить пользователя по ID",
        description = "Возвращает данные пользователя по его уникальному идентификатору"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Пользователь найден",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден",
            content = @Content
        )
    })
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id) {
        UserDto user = userService.getUserById(id);

        // Добавление HATEOAS ссылок
        user.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        user.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        user.add(linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"));
        user.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));

        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(
        summary = "Получить список всех пользователей",
        description = "Возвращает список всех пользователей в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Список пользователей успешно получен",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        )
    })
    public ResponseEntity<CollectionModel<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();

        // Добавление HATEOAS ссылок для каждого пользователя
        for (UserDto user : users) {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
            user.add(linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update"));
            user.add(linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete"));
        }

        // Добавление ссылки на саму коллекцию
        CollectionModel<UserDto> collectionModel = CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Обновить данные пользователя",
        description = "Обновляет данные существующего пользователя по его ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно обновлен",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Некорректные входные данные",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден",
            content = @Content
        )
    })
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID пользователя для обновления", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @Valid @RequestBody UpdateUserDto updateUserDto) {
        UserDto updatedUser = userService.updateUser(id, updateUserDto);

        // Добавление HATEOAS ссылок
        updatedUser.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        updatedUser.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        updatedUser.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Удалить пользователя",
        description = "Удаляет пользователя из системы по его ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Пользователь успешно удален",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя для удаления", required = true, example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
