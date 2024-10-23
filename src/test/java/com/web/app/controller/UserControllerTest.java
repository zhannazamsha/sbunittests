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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ServiceUsageRecordService serviceUsageRecordService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name(UserController.USER_MAIN_PAGE))
                .andExpect(model().attribute(UserController.USERS_ATTRIBUTE, Collections.emptyList()));
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUsersByCategory() throws Exception {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.of(Collections.emptyList()));
        mockMvc.perform(get("/user/non-club-member"))
                .andExpect(status().isOk())
                .andExpect(view().name(UserController.USER_BY_CATEGORY_PAGE))
                .andExpect(model().attribute(UserController.USERS_ATTRIBUTE, Collections.emptyList()));
        verify(userService, times(1)).getUsersByCategory("NON_CLUB_MEMBER");
    }

    @Test
    void testGetUsersByCategory_UserNotFound() throws Exception {
        when(userService.getUsersByCategory("NON_CLUB_MEMBER")).thenReturn(Optional.empty());
        mockMvc.perform(get("/user/non-club-member"))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).getUsersByCategory("NON_CLUB_MEMBER");
    }

    @Test
    void testAddUser() throws Exception {
        User user = new User();
        mockMvc.perform(post("/user/add").flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(UserController.REDIRECT_TO_USER));
        verify(userService, times(1)).saveUser(user);
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(view().name(UserController.USER_DETAILS_PAGE))
                .andExpect(model().attribute(UserController.USER_ATTRIBUTE, user));
        verify(userService, times(1)).getUserById(1L);
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(1L);
    }

    @Test
    void testGetUserById_UserNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_ServiceUsageRecordNotFound() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isInternalServerError());
        verify(userService, times(1)).getUserById(1L);
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(1L);
    }

    @Test
    void testGetUserByIdWithServices() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.userExistsById(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.of(Collections.emptyList()));
        mockMvc.perform(get("/user/1/service-usage-record"))
                .andExpect(status().isOk())
                .andExpect(view().name(UserController.USER_SERVICE_USAGE_RECORD_DETAILS_PAGE))
                .andExpect(model().attribute(UserController.USER_ATTRIBUTE, user));
        verify(userService, times(1)).userExistsById(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(1L);
    }

    @Test
    void testGetUserByIdWithServices_UserNotFound() throws Exception {
        when(userService.userExistsById(anyLong())).thenReturn(false);
        mockMvc.perform(get("/user/1/service-usage-record"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(UserController.REDIRECT_TO_USER));
        verify(userService, times(1)).userExistsById(1L);
    }

    @Test
    void testGetUserEditPage() throws Exception {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        mockMvc.perform(get("/user/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name(UserController.USER_EDIT_PAGE))
                .andExpect(model().attribute(UserController.USER_ATTRIBUTE, user));
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserEditPage_UserNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/user/1/edit"))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/user/1/edit").flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(UserController.REDIRECT_TO_USER));
        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).saveUser(user);
    }

    @Test
    void testUpdateUser_UserNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(post("/user/1/edit").flashAttr("user", new User()))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = new User();
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/user/1/remove"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(UserController.REDIRECT_TO_USER));
        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).deleteUser(user);
    }

    @Test
    void testDeleteUser_UserNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(post("/user/1/remove"))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).getUserById(1L);
    }

    // Additional tests

    @Test
    void testGetUserByIdWithInvalidId() throws Exception {
        mockMvc.perform(get("/user/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddUserWithInvalidData() throws Exception {
        User invalidUser = new User(); // Assuming some constraints are violated
        mockMvc.perform(post("/user/add").flashAttr("user", invalidUser))
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).saveUser(invalidUser);
    }

    @Test
    void testUpdateUserWithInvalidData() throws Exception {
        User invalidUser = new User(); // Assuming some constraints are violated
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(new User()));
        mockMvc.perform(post("/user/1/edit").flashAttr("user", invalidUser))
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).saveUser(invalidUser);
    }

    @Test
    void testDeleteUserWithInvalidId() throws Exception {
        mockMvc.perform(post("/user/invalid/remove"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserByIdWithServices_ServiceUsageRecordNotFound() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.userExistsById(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUsageRecordService.getServiceUsageRecordByUserId(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/user/1/service-usage-record"))
                .andExpect(status().isInternalServerError());
        verify(userService, times(1)).userExistsById(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(serviceUsageRecordService, times(1)).getServiceUsageRecordByUserId(1L);
    }

    @Test
    void testGetUserDetailsWithInvalidId() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/user/invalid/details"))
                .andExpect(status().isBadRequest());
    }
}