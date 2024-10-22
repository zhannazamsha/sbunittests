package com.web.app.utils;

import com.web.app.model.ServiceUsageRecord;
import java.util.Comparator;

/**
 * This class is responsible for comparing two ServiceUsageRecord objects based on their service date.
 * It implements the Comparator interface and overrides the compare method.
 * It is used for sorting ServiceUsageRecord objects in ascending order of their service date.
 */
public class ServiceConsumedDateComparator implements Comparator<ServiceUsageRecord> {

    @Override
    public int compare(ServiceUsageRecord o1, ServiceUsageRecord o2) {
        return o1.getServiceDate().isBefore(o2.getServiceDate()) ? -1 : o1.getServiceDate().isEqual(o2.getServiceDate()) ? 0 : 1;
    }
}
