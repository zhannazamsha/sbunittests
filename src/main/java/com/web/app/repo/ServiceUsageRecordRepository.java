package com.web.app.repo;

import com.web.app.model.ServiceUsageRecord;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ServiceUsageRecordRepository extends CrudRepository<ServiceUsageRecord, Long> {

    Optional<List<ServiceUsageRecord>> getClientDailyServiceConsumedByUserId(long id);
}
