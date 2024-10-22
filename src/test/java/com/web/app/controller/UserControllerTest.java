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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAllUsers();
        verify(model, times(1)).addAttribute(eq("users"), any());
    }

    @Test
    void testGetUsersByCategory() throws Exception {
        when(userService.getUsersByCategory(anyString())).thenReturn(Optional.of(Collections.emptyList()));

        mockMvc.perform(get("/user/non-club-member"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUsersByCategory(eq("NON_CLUB_MEMBER"));
        verify(model, times(1)).addAttribute(eq("users"), any());
    }

    @Test
    void testAddUser() throws Exception {
        mockMvc.perform(post("/user/add")
                        .param("name", "John")
                        .param("surname", "Doe"))
                .andExpect(status().is3xxRedirection());

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserById(eq(1L));
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(eq(1L));
        verify(model, times(1)).addAttribute(eq("user"), any());
    }

    @Test
    void testGetUserByIdWithServices() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.userExistsById(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));

        mockMvc.perform(get("/user/1/service-usage-record"))
                .andExpect(status().isOk());

        verify(userService, times(1)).userExistsById(eq(1L));
        verify(userService, times(1)).getUserById(eq(1L));
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(eq(1L));
        verify(model, times(1)).addAttribute(eq("user"), any());
    }

    @Test
    void testGetUserEditPage() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/user/1/edit"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserById(eq(1L));
        verify(model, times(1)).addAttribute(eq("user"), any());
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/user/1/edit")
                        .param("name", "John")
                        .param("surname", "Doe"))
                .andExpect(status().is3xxRedirection());

        verify(userService, times(1)).getUserById(eq(1L));
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/user/1/remove"))
                .andExpect(status().is3xxRedirection());

        verify(userService, times(1)).getUserById(eq(1L));
        verify(userService, times(1)).deleteUser(any(User.class));
    }
}