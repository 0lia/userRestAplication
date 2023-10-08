package com.example.userRestAplication.service;

import com.example.userRestAplication.dto.UserDTOForPartialUpdate;
import com.example.userRestAplication.entity.User;
import com.example.userRestAplication.exception.UserNotCreatedException;
import com.example.userRestAplication.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService = new UserService();
    private User user;

    @BeforeEach
    void init(){
        user = User.builder()
                .email("user1@mail.com")
                .firstName("Firstname")
                .lastName("Lastname")
                .birthDate(LocalDate.of(1998, 10, 10))
                .address("Lviv, Shevchenka street 23")
                .phoneNumber("")
                .build();
    }

    @Test
    void createTest(){
        assertEquals(user, userService.create(user));
    }

    @Test
    void createInvalidUserTest(){
        user.setBirthDate(LocalDate.of(2028, 10, 3));
        assertThrows(UserNotCreatedException.class, () -> userService.create(user));
    }

    @Test
    void deleteTest(){
        User created = userService.create(user);
        userService.delete(created.getId());
        assertTrue(userService.findByDateRange(LocalDate.MIN, LocalDate.MAX).stream()
                .filter(u -> u.getId() == 1)
                .findFirst().isEmpty()
        );
    }

    @Test
    void deleteNonExistentTest(){
        assertThrows(UserNotFoundException.class, () -> userService.delete(9));
    }

    @Test
    void updateAllFieldsTest(){
        User updated = User.builder()
                .email("user2@mail.com")
                .firstName("Firstname2")
                .lastName("Lastname2")
                .birthDate(LocalDate.of(2000, 10, 10))
                .address("Lviv, Shevchenka street 29")
                .phoneNumber("+380687632144")
                .build();
        User result = userService.findById(userService.updateAllFields(userService.create(user).getId(), updated).getId());
        updated.setId(result.getId());
        assertEquals(updated, result);
    }

    @Test
    void updateAllFieldsNonExistentUserTest(){
        assertThrows(UserNotFoundException.class, () -> userService.updateAllFields(9, user));
    }

    @Test
    void updateTest(){
        UserDTOForPartialUpdate updated = UserDTOForPartialUpdate.builder()
                .email("newEmail@mail.com")
                .lastName("NewLastName")
                .build();

        User result = userService.findById(userService.update(userService.create(user).getId(), updated).getId());
        assertEquals(updated.getEmail(), result.getEmail());
        assertEquals(updated.getLastName(), result.getLastName());
    }

    @Test
    void updateInvalidDataTest(){
        UserDTOForPartialUpdate updated = UserDTOForPartialUpdate.builder()
                .birthDate(LocalDate.of(2028, 10, 4))
                .build();

        assertThrows(UserNotCreatedException.class, () -> userService.update(userService.create(user).getId(), updated));
    }

    @Test
    void findByDateRangeTest(){
        userService.create(user);
        assertTrue(userService
                .findByDateRange(LocalDate.of(1998, 10, 10), LocalDate.of(1999, 10, 10))
                .size() > 0);
    }

    @Test
    void findByDateRangeInvalidArgumentsTest(){
        assertThrows(IllegalArgumentException.class, () -> userService
                .findByDateRange(LocalDate.of(2000, 10, 10), LocalDate.of(1999, 10, 10)));
    }

    @Test
    void findByDateRangeWithNoUsersTest(){
        userService.create(user);
        assertTrue(userService
                .findByDateRange(LocalDate.of(2020, 10, 10), LocalDate.of(2023, 10, 10))
                .size() == 0);
    }

}
