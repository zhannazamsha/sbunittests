package com.web.app.controller;

import com.web.app.exception.UserNotFoundException;
import com.web.app.model.ServiceUsageRecord;
import com.web.app.model.User;
import com.web.app.service.ServiceUsageRecordService;
import com.web.app.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for handling HTTP requests related to User.
 * It interacts with the UserService and ServiceUsageRecordService to retrieve, create, update,
 * and delete Users and their associated ServiceUsageRecords.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    protected static final String USER_MAIN_PAGE = "user-main";
    protected static final String USER_BY_CATEGORY_PAGE = "user-by-category";
    protected static final String REDIRECT_TO_USER = "redirect:/user";
    protected static final String USER_DETAILS_PAGE = "user-details";
    protected static final String USER_EDIT_PAGE = "user-edit";
    protected static final String USER_ATTRIBUTE = "user";
    protected static final String USERS_ATTRIBUTE = "users";
    protected static final String USER_SERVICE_USAGE_RECORD_DETAILS_PAGE = "user-service-usage-record-details";

    private final UserService userService;
    private final ServiceUsageRecordService serviceUsageRecordService;

    public UserController(UserService userService, ServiceUsageRecordService serviceUsageRecordService) {
        this.userService = userService;
        this.serviceUsageRecordService = serviceUsageRecordService;
    }

    @GetMapping
    public String getUsers(Model model) {
        model.addAttribute(USERS_ATTRIBUTE, userService.getAllUsers());
        return USER_MAIN_PAGE;
    }

    @GetMapping("/non-club-member")
    @ApiOperation(value = "get users", notes = "Get users by category NON_CLUB_MEMBER")
    public String getUsersByCategory(Model model) {
        List<User> users = userService.getUsersByCategory("NON_CLUB_MEMBER")
                .orElseThrow(() -> new UserNotFoundException("Users not found"));
        model.addAttribute(USERS_ATTRIBUTE, users);
        return USER_BY_CATEGORY_PAGE;
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return REDIRECT_TO_USER;
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable("id") long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<ServiceUsageRecord> serviceUsageRecords = serviceUsageRecordService
                .getServiceUsageRecordByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("ServiceUsageRecord not found"));
        user.setServiceUsageRecords(serviceUsageRecords);
        model.addAttribute(USER_ATTRIBUTE, user);
        return USER_DETAILS_PAGE;
    }

    @GetMapping("/{id}/service-usage-record")
    public String getUserByIdWithServices(@PathVariable(value = "id") long id, Model model) {
        if (!userService.userExistsById(id)) {
            return REDIRECT_TO_USER;
        }
        Optional<User> user = userService.getUserById(id);
        List<ServiceUsageRecord> serviceUsageRecords = serviceUsageRecordService.
                getServiceUsageRecordByUserId(user.get().getId()).orElseThrow();
        user.get().setServiceUsageRecords(serviceUsageRecords);
        model.addAttribute(USER_ATTRIBUTE, user.get());
        return USER_SERVICE_USAGE_RECORD_DETAILS_PAGE;
    }


    @GetMapping("/{id}/edit")
    public String getUserEditPage(@PathVariable("id") long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        model.addAttribute(USER_ATTRIBUTE, user);
        return USER_EDIT_PAGE;
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable("id") long id, @ModelAttribute User user) {
        User existingUser = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        existingUser.setSurname(user.getSurname());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());
        existingUser.setGender(user.getGender());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        existingUser.setCategory(user.getCategory());
        existingUser.setMoreInformation(user.getMoreInformation());
        userService.saveUser(existingUser);
        return REDIRECT_TO_USER;
    }

    @PostMapping("/{id}/remove")
    public String deleteUser(@PathVariable("id") long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userService.deleteUser(user);
        return REDIRECT_TO_USER;
    }
}
