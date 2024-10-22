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
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() {
        List<User> users = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(users);

        String viewName = userController.getUsers(model);

        verify(model, times(1)).addAttribute(UserController.USERS_ATTRIBUTE, users);
        assertEquals(UserController.USER_MAIN_PAGE, viewName);
    }

    @Test
    public void testGetUsersByCategory() {
        List<User> users = new ArrayList<>();
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(users));

        String viewName = userController.getUsersByCategory(model);

        verify(model, times(1)).addAttribute(UserController.USERS_ATTRIBUTE, users);
        assertEquals(UserController.USER_BY_CATEGORY_PAGE, viewName);
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

        verify(userService, times(1)).saveUser(user);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    public void testGetUserById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        List<ServiceUsageRecord> serviceUsageRecords = new ArrayList<>();

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(userId)).thenReturn(Optional.of(serviceUsageRecords));

        String viewName = userController.getUserById(userId, model);

        verify(model, times(1)).addAttribute(UserController.USER_ATTRIBUTE, user);
        assertEquals(UserController.USER_DETAILS_PAGE, viewName);
    }

    @Test
    public void testGetUserById_UserNotFound() {
        long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userController.getUserById(userId, model);
        });
    }

    @Test
    public void testGetUserByIdWithServices() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        List<ServiceUsageRecord> serviceUsageRecords = new ArrayList<>();

        when(userService.userExistsById(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(userId)).thenReturn(Optional.of(serviceUsageRecords));

        String viewName = userController.getUserByIdWithServices(userId, model);

        verify(model, times(1)).addAttribute(UserController.USER_ATTRIBUTE, user);
        assertEquals(UserController.USER_SERVICE_USAGE_RECORD_DETAILS_PAGE, viewName);
    }

    @Test
    public void testGetUserEditPage() {
        long userId = 1L;
        User user = new User();

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        String viewName = userController.getUserEditPage(userId, model);

        verify(model, times(1)).addAttribute(UserController.USER_ATTRIBUTE, user);
        assertEquals(UserController.USER_EDIT_PAGE, viewName);
    }

    @Test
    public void testUpdateUser() {
        long userId = 1L;
        User user = new User();
        User existingUser = new User();

        when(userService.getUserById(userId)).thenReturn(Optional.of(existingUser));

        String viewName = userController.updateUser(userId, user);

        verify(userService, times(1)).saveUser(existingUser);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    public void testDeleteUser() {
        long userId = 1L;
        User user = new User();

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        String viewName = userController.deleteUser(userId);

        verify(userService, times(1)).deleteUser(user);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }
}