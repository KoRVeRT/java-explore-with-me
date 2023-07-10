package ru.practicum.ewm.main.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.dto.UserMapper;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsersByIds(Set<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids != null && !ids.isEmpty()) {
            return userRepository.findAllById(ids)
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll(pageable)
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User savedUser = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        checkUserExistsById(id);
        userRepository.deleteById(id);
    }

    private void checkUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("User with id=%d was not found", id));
        }
    }
}