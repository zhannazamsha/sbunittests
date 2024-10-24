package com.web.app.controller;

import com.web.app.exception.UserNotFoundException;
import com.web.app.model.User;
import com.web.app.service.ServiceUsageRecordService;
import com.web.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.Collections;
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

    @InjectMocks
    private UserController userController;

    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        model = new BindingAwareModelMap();
    }

    @Test
    public void testGetUsers() {
        List<User> users = Collections.singletonList(new User());
        when(userService.getAllUsers()).thenReturn(users);

        String viewName = userController.getUsers(model);

        assertEquals(UserController.USER_MAIN_PAGE, viewName);
        assertEquals(users, model.getAttribute(UserController.USERS_ATTRIBUTE));
    }

    @Test
    public void testGetUsersByCategory() {
        List<User> users = Collections.singletonList(new User());
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(users));

        String viewName = userController.getUsersByCategory(model);

        assertEquals(UserController.USER_BY_CATEGORY_PAGE, viewName);
        assertEquals(users, model.getAttribute(UserController.USERS_ATTRIBUTE));
    }

    @Test
    public void testGetUsersByCategory_UserNotFoundException() {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userController.getUsersByCategory(model));
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
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(1L)).thenReturn(Optional.of(Collections.emptyList()));

        String viewName = userController.getUserById(1L, model);

        assertEquals(UserController.USER_DETAILS_PAGE, viewName);
        assertEquals(user, model.getAttribute(UserController.USER_ATTRIBUTE));
    }

    @Test
    public void testGetUserById_UserNotFoundException() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userController.getUserById(1L, model));
    }

    @Test
    public void testGetUserByIdWithServices() {
        User user = new User();
        user.setId(1L);
        when(userService.userExistsById(1L)).thenReturn(true);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(1L)).thenReturn(Optional.of(Collections.emptyList()));

        String viewName = userController.getUserByIdWithServices(1L, model);

        assertEquals(UserController.USER_SERVICE_USAGE_RECORD_DETAILS_PAGE, viewName);
        assertEquals(user, model.getAttribute(UserController.USER_ATTRIBUTE));
    }

    @Test
    public void testGetUserEditPage() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        String viewName = userController.getUserEditPage(1L, model);

        assertEquals(UserController.USER_EDIT_PAGE, viewName);
        assertEquals(user, model.getAttribute(UserController.USER_ATTRIBUTE));
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        User existingUser = new User();
        when(userService.getUserById(1L)).thenReturn(Optional.of(existingUser));

        String viewName = userController.updateUser(1L, user);

        verify(userService, times(1)).saveUser(existingUser);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        String viewName = userController.deleteUser(1L);

        verify(userService, times(1)).deleteUser(user);
        assertEquals(UserController.REDIRECT_TO_USER, viewName);
    }
}
