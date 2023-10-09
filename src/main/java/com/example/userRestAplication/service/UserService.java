package com.example.userRestAplication.service;

import com.example.userRestAplication.dto.UserDTOForPartialUpdate;
import com.example.userRestAplication.entity.User;
import com.example.userRestAplication.exception.UserNotCreatedException;
import com.example.userRestAplication.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@PropertySource("application.properties")
public class UserService {
    private List<User> users;
    @Value("${min.age}")
    private int minAge;
    private static int id = 1;

    public User create(User user){
        if(users == null)
            users = new ArrayList<>();

        if(Period.between(user.getBirthDate(), LocalDate.now()).getYears() < minAge){
          throw new UserNotCreatedException("User should be more than 18 years old");
        }
        user.setId(id++);
        users.add(user);
        return user;
    }

    public void delete(int id){
        if(users == null || users.stream().filter(user -> user.getId() == id).findFirst().isEmpty())
            throw new UserNotFoundException("User with id " + id + " doesn't exist");
        users = users.stream().filter(user -> user.getId() != id)
                .collect(Collectors.toList());
    }

    public User updateAllFields(int userId, User updated){
        User userToUpdate = findById(userId);
        if(!checkAge(updated.getBirthDate())){
            throw new UserNotCreatedException("User should be more than 18 years old");
        }
        userToUpdate.setEmail(updated.getEmail());
        userToUpdate.setFirstName(updated.getFirstName());
        userToUpdate.setLastName(updated.getLastName());
        userToUpdate.setBirthDate(updated.getBirthDate());
        if(updated.getAddress() != null)
            userToUpdate.setAddress(updated.getAddress());
        if(updated.getPhoneNumber() != null)
            userToUpdate.setPhoneNumber(updated.getPhoneNumber());
        return userToUpdate;
    }

    public User update(int userId, UserDTOForPartialUpdate updated){
        User userToUpdate = findById(userId);
        if(updated.getEmail() != null)
            userToUpdate.setEmail(updated.getEmail());
        if(updated.getFirstName() != null)
            userToUpdate.setFirstName(updated.getFirstName());
        if(updated.getLastName() != null)
            userToUpdate.setLastName(updated.getLastName());
        if(updated.getBirthDate() != null ) {
            if(!checkAge(updated.getBirthDate())){
                throw new UserNotCreatedException("User should be more than 18 years old");
            }
            userToUpdate.setBirthDate(updated.getBirthDate());
        }
        if(updated.getAddress() != null)
            userToUpdate.setAddress(updated.getAddress());
        if(updated.getPhoneNumber() != null)
            userToUpdate.setPhoneNumber(updated.getPhoneNumber());
        return userToUpdate;
    }

    private boolean checkAge(LocalDate birthDate){
        if(Period.between(birthDate, LocalDate.now()).getYears() < minAge)
            return false;
        return true;
    }

    public User findById(int id){
        Optional<User> found = null;
        if(users == null || (found = users.stream().filter(user -> user.getId() == id).findFirst()).isEmpty())
            throw new UserNotFoundException("User with id " + id + " doesn't exist");
        return found.get();

    }

    public List<User> findByDateRange(LocalDate from, LocalDate to){
        if(from.isAfter(to))
            throw new IllegalArgumentException("\"from\" should be less than \"to\"");
        if(users == null)
            return Collections.emptyList();
        return users.stream()
                .filter(u -> ! u.getBirthDate().isBefore(from))
                .filter(u -> ! u.getBirthDate().isAfter(to))
                .collect(Collectors.toList());
    }

}
