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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ServiceUsageRecordService serviceUsageRecordService;

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = Arrays.asList(new User(), new User());
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-main"))
                .andExpect(model().attributeExists("users"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUsersByCategory() throws Exception {
        List<User> users = Arrays.asList(new User(), new User());
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(users));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/non-club-member"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-by-category"))
                .andExpect(model().attributeExists("users"));

        verify(userService, times(1)).getUsersByCategory("NON_CLUB_MEMBER");
    }

    @Test
    public void testAddUser() throws Exception {
        User user = new User();

        mockMvc.perform(MockMvcRequestBuilders.post("/user/add")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));

        verify(userService, times(1)).saveUser(user);
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        List<ServiceUsageRecord> serviceUsageRecords = Arrays.asList(new ServiceUsageRecord(), new ServiceUsageRecord());
        user.setServiceUsageRecords(serviceUsageRecords);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(1L)).thenReturn(Optional.of(serviceUsageRecords));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-details"))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).getUserById(1L);
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(1L);
    }

    @Test
    public void testGetUserByIdWithServices() throws Exception {
        User user = new User();
        user.setId(1L);
        List<ServiceUsageRecord> serviceUsageRecords = Arrays.asList(new ServiceUsageRecord(), new ServiceUsageRecord());
        user.setServiceUsageRecords(serviceUsageRecords);

        when(userService.userExistsById(1L)).thenReturn(true);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(1L)).thenReturn(Optional.of(serviceUsageRecords));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1/service-usage-record"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-service-usage-record-details"))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).userExistsById(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(1L);
    }

    @Test
    public void testGetUserEditPage() throws Exception {
        User user = new User();
        user.setId(1L);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit"))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        User existingUser = new User();
        existingUser.setId(1L);

        when(userService.getUserById(1L)).thenReturn(Optional.of(existingUser));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/1/edit")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).saveUser(existingUser);
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User();
        user.setId(1L);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/1/remove"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).deleteUser(user);
    }
}
