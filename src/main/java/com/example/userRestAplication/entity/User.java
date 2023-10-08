package com.example.userRestAplication.entity;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class User {

    private int id;
    @NotNull
    @Email(message = "incorrect email")
    private String email;
    @NotEmpty
    @NotBlank(message = "first name is required")
    private String firstName;
    @NotBlank(message = "last name is required")
    private String lastName;
    @NotNull(message = "birth date is required")
    @Past(message = "value must be earlier than current date")
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
 }
