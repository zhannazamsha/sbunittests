package com.web.app.controller;

import com.web.app.exception.ServiceUsageRecordNotFoundException;
import com.web.app.exception.UserNotFoundException;
import com.web.app.model.ServiceUsageRecord;
import com.web.app.model.User;
import com.web.app.service.ServiceUsageRecordService;
import com.web.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * This class is responsible for handling HTTP requests related to ServiceUsageRecord.
 * It interacts with the ServiceUsageRecordService to handle ServiceUsageRecords.
 * It also interacts with the UserService to retrieve User data.
 */
@Controller
@RequestMapping("/service-usage-record")
public class ServiceUsageRecordController {

    protected static final String SERVICE_USAGE_RECORD_MAIN_PAGE = "service-usage-record-main";
    protected static final String REDIRECT_SERVICE_USAGE_RECORD = "redirect:/service-usage-record";
    protected static final String SERVICE_USAGE_RECORD_DETAILS_PAGE = "service-usage-record-details";
    protected static final String REDIRECT_SERVICE_USAGE_RECORD_ID = "redirect:/service-usage-record/{id}";
    protected static final String SERVICE_USAGE_RECORD_ATTRIBUTE = "serviceUsageRecord";
    protected static final String SERVICE_USAGE_RECORDS_ATTRIBUTE = "serviceUsageRecords";

    private final UserService userService;
    private final ServiceUsageRecordService serviceUsageRecordService;

    public ServiceUsageRecordController(UserService userService, ServiceUsageRecordService serviceUsageRecordService) {
        this.userService = userService;
        this.serviceUsageRecordService = serviceUsageRecordService;
    }

    @GetMapping
    public String getAllServiceUsageRecords(Model model) {
        model.addAttribute(SERVICE_USAGE_RECORDS_ATTRIBUTE, serviceUsageRecordService.getAllServiceUsageRecord());
        return SERVICE_USAGE_RECORD_MAIN_PAGE;
    }

    @PostMapping("/add")
    public String addServiceUsageRecord(@ModelAttribute ServiceUsageRecord serviceUsageRecord,
                                        @ModelAttribute User user) {
        User existingUser = userService.getUserBySurnameAndName(user.getSurname(), user.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        ServiceUsageRecord serviceUsageRecordToInput = new ServiceUsageRecord(
                existingUser.getId(),
                existingUser.getName(),
                existingUser.getSurname(),
                serviceUsageRecord.getServiceDate(),
                serviceUsageRecord.getJumpSet(),
                serviceUsageRecord.getBus(),
                serviceUsageRecord.getTaxi(),
                serviceUsageRecord.getRoom(),
                serviceUsageRecord.getBreakfast(),
                serviceUsageRecord.getLunch(),
                serviceUsageRecord.getDinner());
        serviceUsageRecordService.saveServiceUsageRecord(serviceUsageRecordToInput);
        return REDIRECT_SERVICE_USAGE_RECORD;
    }

    @GetMapping("/{id}")
    public String getServiceUsageRecordById(@PathVariable(value = "id") long id, Model model) {
        ServiceUsageRecord serviceUsageRecord = serviceUsageRecordService.getServiceUsageRecordById(id)
                .orElseThrow(() -> new ServiceUsageRecordNotFoundException("Service Usage Record not found"));
        model.addAttribute(SERVICE_USAGE_RECORD_ATTRIBUTE, serviceUsageRecord);
        return SERVICE_USAGE_RECORD_DETAILS_PAGE;
    }

    @PostMapping("/{id}/edit")
    public String updateServiceUsageRecord(@PathVariable(value = "id") long id,
                                           @ModelAttribute ServiceUsageRecord serviceUsageRecord) {
        ServiceUsageRecord existingServiceUsageRecord = serviceUsageRecordService.getServiceUsageRecordById(id)
            .orElseThrow(() -> new ServiceUsageRecordNotFoundException("Service Usage Record not found"));
        existingServiceUsageRecord.setUserId(serviceUsageRecord.getUserId());
        existingServiceUsageRecord.setServiceDate(serviceUsageRecord.getServiceDate());
        existingServiceUsageRecord.setJumpSet(serviceUsageRecord.getJumpSet());
        existingServiceUsageRecord.setBus(serviceUsageRecord.getBus());
        existingServiceUsageRecord.setTaxi(serviceUsageRecord.getTaxi());
        existingServiceUsageRecord.setRoom(serviceUsageRecord.getRoom());
        existingServiceUsageRecord.setBreakfast(serviceUsageRecord.getBreakfast());
        existingServiceUsageRecord.setLunch(serviceUsageRecord.getLunch());
        existingServiceUsageRecord.setDinner(serviceUsageRecord.getDinner());
        serviceUsageRecordService.saveServiceUsageRecord(serviceUsageRecord);
        return REDIRECT_SERVICE_USAGE_RECORD_ID;
    }

    @PostMapping("/{id}/remove")
    public String deleteServiceUsageRecord(@PathVariable(value = "id") long id) {
        ServiceUsageRecord serviceUsageRecord = serviceUsageRecordService.getServiceUsageRecordById(id)
            .orElseThrow(() -> new ServiceUsageRecordNotFoundException("Service Usage Record not found"));
        serviceUsageRecordService.deleteServiceUsageRecord(serviceUsageRecord);
        return REDIRECT_SERVICE_USAGE_RECORD;
    }
}
