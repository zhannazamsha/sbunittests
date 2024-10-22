package com.web.app.controller;

import com.web.app.exception.UserNotFoundException;
import com.web.app.model.ServiceUsageRecord;
import com.web.app.model.User;
import com.web.app.service.ServiceUsageRecordService;
import com.web.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ServiceUsageRecordService serviceUsageRecordService;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetUsers() {
        List<User> users = Collections.singletonList(new User());
        when(userService.getAllUsers()).thenReturn(users);

        String viewName = userController.getUsers(model);

        verify(model).addAttribute("users", users);
        assertEquals("user-main", viewName);
    }

    @Test
    public void testGetUsersByCategory() {
        List<User> users = Collections.singletonList(new User());
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(users));

        String viewName = userController.getUsersByCategory(model);

        verify(model).addAttribute("users", users);
        assertEquals("user-by-category", viewName);
    }

    @Test
    public void testGetUsersByCategory_UserNotFound() {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userController.getUsersByCategory(model);
        });
    }

    @Test
    public void testAddUser() {
        User user = new User();

        String viewName = userController.addUser(user);

        verify(userService).saveUser(user);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setId(1L);
        List<ServiceUsageRecord> records = Collections.singletonList(new ServiceUsageRecord());
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(1L)).thenReturn(Optional.of(records));

        String viewName = userController.getUserById(1L, model);

        verify(model).addAttribute("user", user);
        assertEquals("user-details", viewName);
    }

    @Test
    public void testGetUserById_UserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userController.getUserById(1L, model);
        });
    }

    @Test
    public void testGetUserByIdWithServices() {
        User user = new User();
        user.setId(1L);
        List<ServiceUsageRecord> records = Collections.singletonList(new ServiceUsageRecord());
        when(userService.userExistsById(1L)).thenReturn(true);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(1L)).thenReturn(Optional.of(records));

        String viewName = userController.getUserByIdWithServices(1L, model);

        verify(model).addAttribute("user", user);
        assertEquals("user-service-usage-record-details", viewName);
    }

    @Test
    public void testGetUserEditPage() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        String viewName = userController.getUserEditPage(1L, model);

        verify(model).addAttribute("user", user);
        assertEquals("user-edit", viewName);
    }

    @Test
    public void testUpdateUser() {
        User existingUser = new User();
        when(userService.getUserById(1L)).thenReturn(Optional.of(existingUser));

        User updatedUser = new User();
        updatedUser.setSurname("Doe");

        String viewName = userController.updateUser(1L, updatedUser);

        verify(userService).saveUser(existingUser);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        String viewName = userController.deleteUser(1L);

        verify(userService).deleteUser(user);
        assertEquals("redirect:/user", viewName);
    }
}