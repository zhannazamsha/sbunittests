package com.web.app.service;

import com.web.app.model.ServiceUsageRecord;
import com.web.app.repo.ServiceUsageRecordRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class is responsible for handling the business logic related to ServiceUsageRecord.
 * It interacts with the ServiceUsageRecordRepository to handle ServiceUsageRecord data.
 */
@Service
public class ServiceUsageRecordService {

    @Autowired
    private ServiceUsageRecordRepository serviceUsageRecordRepository;

    public Iterable<ServiceUsageRecord> getAllServiceUsageRecord() {
        return serviceUsageRecordRepository.findAll();
    }

    public ServiceUsageRecord saveServiceUsageRecord (ServiceUsageRecord serviceUsageRecord) {
        return serviceUsageRecordRepository.save(serviceUsageRecord);
    }

    public boolean serviceUsageRecordExistsById(long id) {
        return serviceUsageRecordRepository.existsById(id);
    }

    public Optional<ServiceUsageRecord> getServiceUsageRecordById(long id) {
        return serviceUsageRecordRepository.findById(id);
    }

    public Optional<ServiceUsageRecord> getServiceUsageRecordByClientId(long id) {
        return serviceUsageRecordRepository.findById(id);
    }

    public void deleteServiceUsageRecord(ServiceUsageRecord serviceUsageRecord) {
        serviceUsageRecordRepository.delete(serviceUsageRecord);
    }

    public Optional<List<ServiceUsageRecord>> getServiceUsageRecordByUserId(long id) {
        return serviceUsageRecordRepository.getClientDailyServiceConsumedByUserId(id);
    }
}
