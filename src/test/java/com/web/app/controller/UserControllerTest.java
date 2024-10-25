package com.web.app.controller;

import com.web.app.exception.UserNotFoundException;
import com.web.app.model.ServiceUsageRecord;
import com.web.app.model.User;
import com.web.app.service.ServiceUsageRecordService;
import com.web.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig
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
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");

        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-main"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    public void testGetUsersByCategory() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");

        Mockito.when(userService.getUsersByCategory(eq("NON_CLUB_MEMBER"))).thenReturn(Optional.of(Arrays.asList(user)));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/non-club-member"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-by-category"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    public void testAddUser() throws Exception {
        User user = new User();
        user.setName("John");
        user.setSurname("Doe");

        Mockito.when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/add")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");

        ServiceUsageRecord serviceUsageRecord = new ServiceUsageRecord();
        serviceUsageRecord.setId(1L);

        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong()))
                .thenReturn(Optional.of(Arrays.asList(serviceUsageRecord)));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-details"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testGetUserByIdWithServices() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");

        ServiceUsageRecord serviceUsageRecord = new ServiceUsageRecord();
        serviceUsageRecord.setId(1L);

        Mockito.when(userService.userExistsById(anyLong())).thenReturn(true);
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong()))
                .thenReturn(Optional.of(Arrays.asList(serviceUsageRecord)));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1/service-usage-record"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-service-usage-record-details"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testGetUserEditPage() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");

        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");

        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/1/edit")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");

        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userService).deleteUser(any(User.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/1/remove"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));
    }

    @Test
    public void testGetUsersByCategoryNotFound() throws Exception {
        Mockito.when(userService.getUsersByCategory(eq("NON_CLUB_MEMBER")))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/user/non-club-member"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserByIdWithServicesNotFound() throws Exception {
        Mockito.when(userService.userExistsById(anyLong())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1/service-usage-record"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"));
    }
}
