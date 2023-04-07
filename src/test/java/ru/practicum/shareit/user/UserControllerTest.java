package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    @Autowired
    private final ObjectMapper mapper;
    @Autowired
    private final MockMvc mvc;

    @MockBean
    UserService userService;
    private final UserDto userDto = new UserDto(
            1L,
            "user",
            "user@user.ru");

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDto));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("user"))
                .andExpect(jsonPath("$[0].email").value("user@user.ru"));
    }

    @Test
    void getById_whenUserFound_thenReturnUser() throws Exception {
        when(userService.getById(anyLong())).thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@user.ru"));
    }

    @SneakyThrows
    @Test
    void postUser_whenUserIsValid_thenReturnedOk() {

        when(userService.saveUser(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void postUser_whenUserIsNotValid_thenReturnedBadRequest() {
        UserDto userDto3 = new UserDto(
                3L,
                "user",
                "useruser.ru");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto3)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(userDto3);
    }


    @SneakyThrows
    @Test
    void updateUser_whenUserIsValid_thenReturnedOk() {
        UserDto patchedUser = new UserDto();
        patchedUser.setName("patchedName");
        patchedUser.setEmail(userDto.getEmail());
        patchedUser.setId(1L);

        when(userService.patch(any())).thenReturn(patchedUser);

        mvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patchedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("patchedName"))
                .andExpect(jsonPath("$.email").value("user@user.ru"));



    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIsNotValid_thenReturnedBadRequest() {
        UserDto patchedUser = new UserDto();
        patchedUser.setName("updatedName");
        patchedUser.setEmail("azddv.com");

        mvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(patchedUser)))
                .andExpect(status().isBadRequest());
        verify(userService, never()).saveUser(patchedUser);

    }


    @SneakyThrows
    @Test
    void deleteUser() {
        mvc.perform(delete("/users/{userId}", 1L)).andExpect(status().isOk());

        verify(userService).delete(1L);
    }


    @SneakyThrows
    @Test
    void getUserById_whenUserNotFound_thenReturnedNotFound() {
        Long userId = 9L;

        when(userService.getById(userId)).thenThrow(new UserNotFoundException("message"));

        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService).getById(userId);

    }


}