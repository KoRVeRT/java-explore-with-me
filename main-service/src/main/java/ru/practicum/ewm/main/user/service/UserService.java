package ru.practicum.ewm.main.user.service;

import ru.practicum.ewm.main.user.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<UserDto> getUsersByIds(Set<Long> ids, Integer from, Integer size);

    void deleteUser(Long id);

    UserDto addUser(UserDto userDto);
}