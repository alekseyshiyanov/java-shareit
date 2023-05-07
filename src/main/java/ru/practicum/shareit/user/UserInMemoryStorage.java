package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class UserInMemoryStorage {

    private Long userUID = 0L;
    private final HashMap<Long, User> users = new HashMap<>();

    public List<User> getUsersList() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        emailIsExist(user.getEmail(), null);
        addUser(user);
        return user;
    }

    public User updateUser(User user) {
        Long uid = user.getId();

        User oldUser = getUser(uid);

        log.info("Обновляем объект с ID: {}", uid);
        log.info("Объект до обновления: {}", oldUser);

        User newUser = User.builder()
                        .id(uid)
                        .name((user.getName() == null) ? oldUser.getName() : user.getName())
                        .email((user.getEmail() == null) ? oldUser.getEmail() : user.getEmail())
                        .build();

        emailIsExist(newUser.getEmail(), uid);

        users.put(uid, newUser);

        User currentUser = getUser(uid);

        log.info("Объект после обновления: {}", currentUser);

        return currentUser;
    }

    public User getUser(Long userId) {
        return checkUser(userId);
    }

    public Boolean userIsPresent(Long userId) {
        return users.containsKey(userId);
    }

    public void deleteUser(Long userId) {
        if (users.remove(userId) == null) {
            log.error("Пользователь с ID = {} не существует", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID = " + userId + " не существует");
        }
    }

    private void addUser(User user) {
        Long uid = getUserUID();

        user.setId(uid);
        users.put(uid, user);

        log.info("Сохранен объект: {}", user);
    }

    private User checkUser(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            log.error("Пользователь с ID = {} не существует", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID = " + userId + " не существует");
        }
        return user;
    }

    private void emailIsExist(String email, Long uid) {
        if (users.values().stream().anyMatch(u -> (u.getEmail().equals(email) && !u.getId().equals(uid)))) {
            log.error("Пользователь с таким email уже существует");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует");
        }
    }

    private Long getUserUID() {
        return ++userUID;
    }
}
