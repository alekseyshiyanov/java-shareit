package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    void deleteUserById(Long UserId);

    User getUserById(Long id);

    @Modifying
    @Transactional
    @Query("update User u " +
            "set u.name  = COALESCE(CAST(:#{#newUser.name}  as string), u.name ), " +
                "u.email = COALESCE(CAST(:#{#newUser.email} as string), u.email)  " +
            "where  u.id = :#{#newUser.id}")
    void updateUser(@Param("newUser") User newUser);
}