package com.example.userRestAplication.controller;

import com.example.userRestAplication.dto.UserDTOForPartialUpdate;
import com.example.userRestAplication.entity.User;
import com.example.userRestAplication.exception.UserNotFoundException;
import com.example.userRestAplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private User user;

    @BeforeEach
    public void init() {
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
    void createValidUserTest() throws Exception {
        String content = convertToJson(user);
        Mockito.when(userService.create(any(User.class))).thenReturn(user);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("email", is(user.getEmail())))
                .andExpect(jsonPath("firstName", is(user.getFirstName())))
                .andExpect(jsonPath("lastName", is(user.getLastName())))
                .andExpect(jsonPath("birthDate", is(user.getBirthDate().toString())))
                .andExpect(jsonPath("address", is(user.getAddress())))
                .andExpect(jsonPath("phoneNumber", is(user.getPhoneNumber())));
    }

    @Test
    void createInvalidUserTest() throws Exception {
        user.setEmail("INVALID EMAIL");
        String content = convertToJson(user);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createInvalidUserTest2() throws Exception {
        user.setBirthDate(LocalDate.of(2027, 9, 30));
        String content = convertToJson(user);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateTest() throws Exception {
        UserDTOForPartialUpdate userDto = UserDTOForPartialUpdate.builder()
                .firstName("New name")
                .phoneNumber("+380678354227")
                .build();
        String content = "{\n" +
                "    \"firstName\": \"" + userDto.getFirstName() + "\", \n" +
                "    \"phoneNumber\": \"" + userDto.getPhoneNumber() + "\"\n" +
                "}";
        user.setFirstName(userDto.getFirstName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        Mockito.when(userService.update(any(Integer.class), any(UserDTOForPartialUpdate.class))).thenReturn(user);
        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("firstName", is(user.getFirstName())))
                .andExpect(jsonPath("phoneNumber", is(user.getPhoneNumber())));

    }

    @Test
    void updateInvalidDataTest() throws Exception {
        UserDTOForPartialUpdate userDto = UserDTOForPartialUpdate.builder()
                .email("INVALID EMAIL")
                .build();
        String content = "{\n" +
                "    \"email\": \"" + userDto.getEmail() + "\"\n" +
                "}";
        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)).andExpect(status().is4xxClientError());
    }

    @Test
    void updateAllFieldsTest() throws Exception {
        user.setFirstName("new name");
        user.setEmail("newEmail@mail.com");
        String content = convertToJson(user);
        Mockito.when(userService.updateAllFields(any(Integer.class), any(User.class))).thenReturn(user);
        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("firstName", is(user.getFirstName())))
                .andExpect(jsonPath("email", is(user.getEmail())));
    }

    @Test
    void updateAllFieldsInvalidDataTest() throws Exception {
        user.setLastName("");
        String content = convertToJson(user);
        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteTest() throws Exception {
        doNothing().when(userService).delete(any(Integer.class));
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteNonExistentUserTest() throws Exception {
        doThrow(UserNotFoundException.class).when(userService).delete(any(Integer.class));
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().is4xxClientError());
    }
    private String convertToJson(User user){
        return "{\n" +
                "    \"email\": \"" + user.getEmail() + "\",\n" +
                "    \"firstName\": \"" + user.getFirstName() + "\", \n" +
                "    \"lastName\": \"" + user.getLastName() + "\",\n" +
                "    \"birthDate\": \"" + user.getBirthDate() + "\",\n" +
                "    \"address\": \"" + user.getAddress() + "\",\n" +
                "    \"phoneNumber\": \"" + user.getPhoneNumber() + "\"\n" +
                "}";
    }
}
