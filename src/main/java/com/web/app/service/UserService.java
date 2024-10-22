package com.web.app.service;

import com.web.app.model.User;
import com.web.app.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for handling the business logic related to User.
 * It interacts with the UserRepository to handle User data.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean userExistsById(long id) {
        return userRepository.existsById(id);
    }

    public Optional<User> getUserById(long id) { //name the same or not?
        return userRepository.findById(id);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public Optional<User> getUserBySurnameAndName(String surname, String name) {
        return userRepository.getUserBySurnameAndName(surname, name);
    }

    public Optional<List<User>> getUsersByCategory(String category) {
        return userRepository.getUsersByCategory(category);
    }

}
