package com.web.app.controller;

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
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() {
        List<User> users = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(users);
        
        String viewName = userController.getUsers(model);
        
        verify(model, times(1)).addAttribute("users", users);
        assertEquals("user-main", viewName);
    }

    @Test
    public void testGetUsersByCategory() {
        List<User> users = new ArrayList<>();
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(users));
        
        String viewName = userController.getUsersByCategory(model);
        
        verify(model, times(1)).addAttribute("users", users);
        assertEquals("user-by-category", viewName);
    }

    @Test
    public void testAddUser() {
        User user = new User();
        
        String viewName = userController.addUser(user);
        
        verify(userService, times(1)).saveUser(user);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(new ArrayList<>()));
        
        String viewName = userController.getUserById(1L, model);
        
        verify(model, times(1)).addAttribute("user", user);
        assertEquals("user-details", viewName);
    }

    @Test
    public void testGetUserByIdWithServices() {
        User user = new User();
        when(userService.userExistsById(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(new ArrayList<>()));
        
        String viewName = userController.getUserByIdWithServices(1L, model);
        
        verify(model, times(1)).addAttribute("user", user);
        assertEquals("user-service-usage-record-details", viewName);
    }

    @Test
    public void testGetUserEditPage() {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        
        String viewName = userController.getUserEditPage(1L, model);
        
        verify(model, times(1)).addAttribute("user", user);
        assertEquals("user-edit", viewName);
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        User existingUser = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(existingUser));
        
        String viewName = userController.updateUser(1L, user);
        
        verify(userService, times(1)).saveUser(existingUser);
        assertEquals("redirect:/user", viewName);
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        
        String viewName = userController.deleteUser(1L);
        
        verify(userService, times(1)).deleteUser(user);
        assertEquals("redirect:/user", viewName);
    }
}
