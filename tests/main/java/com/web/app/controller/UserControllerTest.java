package com.web.app.controller;

import com.web.app.exception.UserNotFoundException;
import com.web.app.model.ServiceUsageRecord;
import com.web.app.model.User;
import com.web.app.service.ServiceUsageRecordService;
import com.web.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ServiceUsageRecordService serviceUsageRecordService;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    @Test
    void testGetUsers() {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        String viewName = userController.getUsers(model);

        verify(model).addAttribute(UserController.USERS_ATTRIBUTE, Collections.singletonList(user));
        assertEquals(UserController.USER_MAIN_PAGE, viewName);
    }

    @Test
    void testGetUsersByCategory() {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(Collections.singletonList(user)));

        String viewName = userController.getUsersByCategory(model);

        verify(model).addAttribute(UserController.USERS_ATTRIBUTE, Collections.singletonList(user));
        assertEquals(UserController.USER_BY_CATEGORY_PAGE, viewName);
    }

    @Test
    void testGetUsersByCategoryNotFound() {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.empty());

        try {
            userController.getUsersByCategory(model);
        } catch (UserNotFoundException e) {
            assertEquals("Users not found", e.getMessage());
        }
    }

    @Test
    void testAddUser() {
        String viewName = userController.addUser(user);

        verify(userService).saveUser(user);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    void testGetUserById() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));

        String viewName = userController.getUserById(1L, model);

        verify(model).addAttribute(UserController.USER_ATTRIBUTE, user);
        assertEquals(UserController.USER_DETAILS_PAGE, viewName);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        try {
            userController.getUserById(1L, model);
        } catch (UserNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void testGetUserByIdWithServices() {
        when(userService.userExistsById(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));

        String viewName = userController.getUserByIdWithServices(1L, model);

        verify(model).addAttribute(UserController.USER_ATTRIBUTE, user);
        assertEquals(UserController.USER_SERVICE_USAGE_RECORD_DETAILS_PAGE, viewName);
    }

    @Test
    void testGetUserByIdWithServicesUserNotFound() {
        when(userService.userExistsById(anyLong())).thenReturn(false);

        String viewName = userController.getUserByIdWithServices(1L, model);

        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    void testGetUserEditPage() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        String viewName = userController.getUserEditPage(1L, model);

        verify(model).addAttribute(UserController.USER_ATTRIBUTE, user);
        assertEquals(UserController.USER_EDIT_PAGE, viewName);
    }

    @Test
    void testGetUserEditPageNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        try {
            userController.getUserEditPage(1L, model);
        } catch (UserNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void testUpdateUser() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        String viewName = userController.updateUser(1L, user);

        verify(userService).saveUser(user);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    void testUpdateUserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        try {
            userController.updateUser(1L, user);
        } catch (UserNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void testDeleteUser() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        String viewName = userController.deleteUser(1L);

        verify(userService).deleteUser(user);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        try {
            userController.deleteUser(1L);
        } catch (UserNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }
}
