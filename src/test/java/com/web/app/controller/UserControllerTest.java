package com.web.app.controller;

import com.web.app.model.User;
import com.web.app.service.ServiceUsageRecordService;
import com.web.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private ServiceUsageRecordService serviceUsageRecordService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(new User(), new User()));
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUsersByCategory() throws Exception {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(Arrays.asList(new User(), new User())));
        mockMvc.perform(get("/user/non-club-member"))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUsersByCategory("NON_CLUB_MEMBER");
    }

    @Test
    void testAddUser() throws Exception {
        mockMvc.perform(post("/user/add").flashAttr("user", new User()))
                .andExpect(status().is3xxRedirection());
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Arrays.asList()));
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUserById(anyLong());
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(anyLong());
    }

    @Test
    void testGetUserByIdWithServices() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.userExistsById(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Arrays.asList()));
        mockMvc.perform(get("/user/1/service-usage-record"))
                .andExpect(status().isOk());
        verify(userService, times(1)).userExistsById(anyLong());
        verify(userService, times(1)).getUserById(anyLong());
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(anyLong());
    }

    @Test
    void testGetUserEditPage() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        mockMvc.perform(get("/user/1/edit"))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/user/1/edit").flashAttr("user", user))
                .andExpect(status().is3xxRedirection());
        verify(userService, times(1)).getUserById(anyLong());
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/user/1/remove"))
                .andExpect(status().is3xxRedirection());
        verify(userService, times(1)).getUserById(anyLong());
        verify(userService, times(1)).deleteUser(any(User.class));
    }
}
