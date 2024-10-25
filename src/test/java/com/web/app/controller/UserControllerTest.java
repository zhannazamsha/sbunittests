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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsers() {
        List<User> users = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(users);
        String viewName = userController.getUsers(model);
        verify(model).addAttribute(UserController.USERS_ATTRIBUTE, users);
        assertEquals(UserController.USER_MAIN_PAGE, viewName);
    }

    @Test
    void testGetUsersByCategory() {
        List<User> users = new ArrayList<>();
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(users));
        String viewName = userController.getUsersByCategory(model);
        verify(model).addAttribute(UserController.USERS_ATTRIBUTE, users);
        assertEquals(UserController.USER_BY_CATEGORY_PAGE, viewName);
    }

    @Test
    void testGetUsersByCategory_UserNotFound() {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userController.getUsersByCategory(model));
    }

    @Test
    void testAddUser() {
        User user = new User();
        String viewName = userController.addUser(user);
        verify(userService).saveUser(user);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1L);
        List<ServiceUsageRecord> serviceUsageRecords = new ArrayList<>();
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(1L)).thenReturn(Optional.of(serviceUsageRecords));
        String viewName = userController.getUserById(1L, model);
        verify(model).addAttribute(UserController.USER_ATTRIBUTE, user);
        assertEquals(UserController.USER_DETAILS_PAGE, viewName);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userController.getUserById(1L, model));
    }

    @Test
    void testGetUserByIdWithServices() {
        User user = new User();
        user.setId(1L);
        List<ServiceUsageRecord> serviceUsageRecords = new ArrayList<>();
        when(userService.userExistsById(1L)).thenReturn(true);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(1L)).thenReturn(Optional.of(serviceUsageRecords));
        String viewName = userController.getUserByIdWithServices(1L, model);
        verify(model).addAttribute(UserController.USER_ATTRIBUTE, user);
        assertEquals(UserController.USER_SERVICE_USAGE_RECORD_DETAILS_PAGE, viewName);
    }

    @Test
    void testGetUserByIdWithServices_UserNotFound() {
        when(userService.userExistsById(1L)).thenReturn(false);
        String viewName = userController.getUserByIdWithServices(1L, model);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    void testGetUserEditPage() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        String viewName = userController.getUserEditPage(1L, model);
        verify(model).addAttribute(UserController.USER_ATTRIBUTE, user);
        assertEquals(UserController.USER_EDIT_PAGE, viewName);
    }

    @Test
    void testGetUserEditPage_UserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userController.getUserEditPage(1L, model));
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User();
        User updatedUser = new User();
        when(userService.getUserById(1L)).thenReturn(Optional.of(existingUser));
        String viewName = userController.updateUser(1L, updatedUser);
        verify(userService).saveUser(existingUser);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        User updatedUser = new User();
        assertThrows(UserNotFoundException.class, () -> userController.updateUser(1L, updatedUser));
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        String viewName = userController.deleteUser(1L);
        verify(userService).deleteUser(user);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userController.deleteUser(1L));
    }
}
