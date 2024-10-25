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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ServiceUsageRecordService serviceUsageRecordService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-main"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUsersByCategory() throws Exception {
        List<User> users = new ArrayList<>();
        when(userService.getUsersByCategory(anyString())).thenReturn(Optional.of(users));

        mockMvc.perform(get("/user/non-club-member"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-by-category"));

        verify(userService, times(1)).getUsersByCategory(anyString());
    }

    @Test
    public void testAddUser() throws Exception {
        doNothing().when(userService).saveUser(any(User.class));

        mockMvc.perform(post("/user/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User();
        List<ServiceUsageRecord> serviceUsageRecords = new ArrayList<>();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(serviceUsageRecords));

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-details"));

        verify(userService, times(1)).getUserById(anyLong());
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(anyLong());
    }

    @Test
    public void testGetUserByIdWithServices() throws Exception {
        User user = new User();
        List<ServiceUsageRecord> serviceUsageRecords = new ArrayList<>();
        when(userService.userExistsById(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(serviceUsageRecords));

        mockMvc.perform(get("/user/1/service-usage-record"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-service-usage-record-details"));

        verify(userService, times(1)).userExistsById(anyLong());
        verify(userService, times(1)).getUserById(anyLong());
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(anyLong());
    }

    @Test
    public void testGetUserEditPage() throws Exception {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/user/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit"));

        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userService).saveUser(any(User.class));

        mockMvc.perform(post("/user/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"));

        verify(userService, times(1)).getUserById(anyLong());
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userService).deleteUser(any(User.class));

        mockMvc.perform(post("/user/1/remove"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user"));

        verify(userService, times(1)).getUserById(anyLong());
        verify(userService, times(1)).deleteUser(any(User.class));
    }
}
