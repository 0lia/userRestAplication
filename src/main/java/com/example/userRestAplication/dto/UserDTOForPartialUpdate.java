package com.example.userRestAplication.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDTOForPartialUpdate {
    @Email(message = "incorrect email")
    private String email;
    @Size(min = 2, message = "first name should contain minimum 2 symbols")
    private String firstName;
    @Size(min = 2, message = "last name should contain minimum 2 symbols")
    private String lastName;
    @Past(message = "value must be earlier than current date")
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;

}
