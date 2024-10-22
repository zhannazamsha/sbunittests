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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetUsers() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());
        String viewName = userController.getUsers(model);
        assertEquals(UserController.USER_MAIN_PAGE, viewName);
        verify(model, times(1)).addAttribute(eq(UserController.USERS_ATTRIBUTE), any());
    }

    @Test
    void testGetUsersByCategory() {
        when(userService.getUsersByCategory(anyString())).thenReturn(Optional.of(Collections.emptyList()));
        String viewName = userController.getUsersByCategory(model);
        assertEquals(UserController.USER_BY_CATEGORY_PAGE, viewName);
        verify(model, times(1)).addAttribute(eq(UserController.USERS_ATTRIBUTE), any());
    }

    @Test
    void testAddUser() {
        User user = new User();
        String viewName = userController.addUser(user);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
        verify(userService, times(1)).saveUser(user);
    }

    @Test
    void testGetUserById() {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));
        String viewName = userController.getUserById(1L, model);
        assertEquals(UserController.USER_DETAILS_PAGE, viewName);
        verify(model, times(1)).addAttribute(eq(UserController.USER_ATTRIBUTE), any());
    }

    @Test
    void testGetUserByIdWithServices() {
        User user = new User();
        when(userService.userExistsById(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));
        String viewName = userController.getUserByIdWithServices(1L, model);
        assertEquals(UserController.USER_SERVICE_USAGE_RECORD_DETAILS_PAGE, viewName);
        verify(model, times(1)).addAttribute(eq(UserController.USER_ATTRIBUTE), any());
    }

    @Test
    void testGetUserEditPage() {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        String viewName = userController.getUserEditPage(1L, model);
        assertEquals(UserController.USER_EDIT_PAGE, viewName);
        verify(model, times(1)).addAttribute(eq(UserController.USER_ATTRIBUTE), any());
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(existingUser));
        User updatedUser = new User();
        String viewName = userController.updateUser(1L, updatedUser);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
        verify(userService, times(1)).saveUser(existingUser);
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        String viewName = userController.deleteUser(1L);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
        verify(userService, times(1)).deleteUser(user);
    }
}