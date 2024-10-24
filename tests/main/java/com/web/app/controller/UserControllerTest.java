package com.web.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ServiceUsageRecordService serviceUsageRecordService;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userService.getAllUsers()).thenReturn(users);

        String viewName = userController.getUsers(model);

        verify(model).addAttribute("users", users);
        assertEquals("user-main", viewName);
    }

    @Test
    void testGetUsersByCategory() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(users));

        String viewName = userController.getUsersByCategory(model);

        verify(model).addAttribute("users", users);
        assertEquals("user-by-category", viewName);
    }

    @Test
    void testAddUser() {
        User user = new User();

        String viewName = userController.addUser(user);

        verify(userService).saveUser(user);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    void testGetUserById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        List<ServiceUsageRecord> records = Arrays.asList(new ServiceUsageRecord(), new ServiceUsageRecord());

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(userId)).thenReturn(Optional.of(records));

        String viewName = userController.getUserById(userId, model);

        verify(model).addAttribute("user", user);
        assertEquals("user-details", viewName);
    }

    @Test
    void testGetUserByIdWithServices() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        List<ServiceUsageRecord> records = Arrays.asList(new ServiceUsageRecord(), new ServiceUsageRecord());

        when(userService.userExistsById(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(userId)).thenReturn(Optional.of(records));

        String viewName = userController.getUserByIdWithServices(userId, model);

        verify(model).addAttribute("user", user);
        assertEquals("user-service-usage-record-details", viewName);
    }

    @Test
    void testGetUserEditPage() {
        long userId = 1L;
        User user = new User();

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        String viewName = userController.getUserEditPage(userId, model);

        verify(model).addAttribute("user", user);
        assertEquals("user-edit", viewName);
    }

    @Test
    void testUpdateUser() {
        long userId = 1L;
        User user = new User();
        User existingUser = new User();

        when(userService.getUserById(userId)).thenReturn(Optional.of(existingUser));

        String viewName = userController.updateUser(userId, user);

        verify(userService).saveUser(existingUser);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    void testDeleteUser() {
        long userId = 1L;
        User user = new User();

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        String viewName = userController.deleteUser(userId);

        verify(userService).deleteUser(user);
        assertEquals("redirect:/user", viewName);
    }
}
