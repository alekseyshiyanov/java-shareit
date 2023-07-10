package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    void deleteUserById(Long userId);

    Optional<User> getUserById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("update User u " +
            "set u.name  = COALESCE(CAST(:#{#newUser.name}  as string), u.name ), " +
                "u.email = COALESCE(CAST(:#{#newUser.email} as string), u.email)  " +
            "where  u.id = :#{#newUser.id}")
    void updateUser(@Param("newUser") User newUser);
}