package com.example.userRestAplication.controller;

import com.example.userRestAplication.dto.DateRangeDTO;
import com.example.userRestAplication.dto.UserDTOForPartialUpdate;
import com.example.userRestAplication.entity.User;
import com.example.userRestAplication.service.UserService;
import com.example.userRestAplication.exception.UserNotCreatedException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<User> create(@RequestBody @Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error: errors)
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            throw new UserNotCreatedException(errorMsg.toString());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(user));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> update(@PathVariable("userId") Integer userId, @RequestBody @Valid UserDTOForPartialUpdate userDto){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.update(userId, userDto));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateAllFields(@PathVariable("userId") Integer userId, @RequestBody @Valid User user){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateAllFields(userId, user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> delete(@PathVariable("userId") Integer userId){
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("User with id " + userId + " deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<User>> getByDateRange(@RequestBody DateRangeDTO dateRange){
        return ResponseEntity.ok()
                .body(userService.findByDateRange(dateRange.getFrom(), dateRange.getTo()));
    }
}
