package com.web.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class ServiceUsageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private long userId;
    private String userName;
    private String userSurname;
    private LocalDate serviceDate;
    private int jumpSet;
    private double bus;
    private double taxi;
    private int room;
    private int breakfast;
    private int lunch;
    private int dinner;

    public ServiceUsageRecord() {
    }

    public ServiceUsageRecord(long userId, String userName, String userSurname, LocalDate serviceDate, int jumpSet,
                              double bus, double taxi, int room, int breakfast, int lunch, int dinner) {
        this.userId = userId;
        this.userName = userName;
        this.userSurname = userSurname;
        this.serviceDate = serviceDate;
        this.jumpSet = jumpSet;
        this.bus = bus;
        this.taxi = taxi;
        this.room = room;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }
}
