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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ServiceUsageRecordService serviceUsageRecordService;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    private User user;
    private List<User> userList;
    private List<ServiceUsageRecord> serviceUsageRecordList;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");

        userList = new ArrayList<>();
        userList.add(user);

        serviceUsageRecordList = new ArrayList<>();
        serviceUsageRecordList.add(new ServiceUsageRecord());
    }

    @Test
    void testGetUsers() {
        when(userService.getAllUsers()).thenReturn(userList);
        String viewName = userController.getUsers(model);
        verify(model, times(1)).addAttribute("users", userList);
        assertEquals("user-main", viewName);
    }

    @Test
    void testGetUsersByCategory() {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(userList));
        String viewName = userController.getUsersByCategory(model);
        verify(model, times(1)).addAttribute("users", userList);
        assertEquals("user-by-category", viewName);
    }

    @Test
    void testGetUsersByCategoryUserNotFound() {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userController.getUsersByCategory(model));
    }

    @Test
    void testAddUser() {
        String viewName = userController.addUser(user);
        verify(userService, times(1)).saveUser(user);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    void testGetUserById() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(serviceUsageRecordList));
        String viewName = userController.getUserById(1L, model);
        verify(model, times(1)).addAttribute("user", user);
        assertEquals("user-details", viewName);
    }

    @Test
    void testGetUserByIdUserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userController.getUserById(1L, model));
    }

    @Test
    void testGetUserByIdServiceRecordNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userController.getUserById(1L, model));
    }

    @Test
    void testGetUserByIdWithServices() {
        when(userService.userExistsById(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(serviceUsageRecordList));
        String viewName = userController.getUserByIdWithServices(1L, model);
        verify(model, times(1)).addAttribute("user", user);
        assertEquals("user-service-usage-record-details", viewName);
    }

    @Test
    void testGetUserByIdWithServicesUserNotFound() {
        when(userService.userExistsById(anyLong())).thenReturn(false);
        String viewName = userController.getUserByIdWithServices(1L, model);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    void testGetUserEditPage() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        String viewName = userController.getUserEditPage(1L, model);
        verify(model, times(1)).addAttribute("user", user);
        assertEquals("user-edit", viewName);
    }

    @Test
    void testGetUserEditPageUserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userController.getUserEditPage(1L, model));
    }

    @Test
    void testUpdateUser() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        String viewName = userController.updateUser(1L, user);
        verify(userService, times(1)).saveUser(user);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    void testUpdateUserUserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userController.updateUser(1L, user));
    }

    @Test
    void testDeleteUser() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        String viewName = userController.deleteUser(1L);
        verify(userService, times(1)).deleteUser(user);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    void testDeleteUserUserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userController.deleteUser(1L));
    }
}
