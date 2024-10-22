package com.web.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;
    private String surname;
    private LocalDate birthday;
    private String gender;
    private String email;
    private String phone;
    private String role;
    private String category;
    private String moreInformation;
    @OneToMany
    private List<ServiceUsageRecord> serviceUsageRecords;

    public User() {
    }

    public User(long id, String name, String surname, LocalDate birthday, String gender, String email, String phone,
                String role, String category, String moreInformation, List<ServiceUsageRecord> serviceUsageRecords) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.category = category;
        this.moreInformation = moreInformation;
        this.serviceUsageRecords = serviceUsageRecords;
    }

    public User(String name, String surname, LocalDate birthday, String gender, String email, String phone, String role,
                String category, String moreInformation) {
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.category = category;
        this.moreInformation = moreInformation;
    }
}
