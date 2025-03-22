package com.example.user_management.service;

import com.example.user_management.exception.UserExistsException;
import com.example.user_management.exception.UserNotFoundException;
import com.example.user_management.model.User;
import com.example.user_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User createUser(User user) {
        if (userRepository.findByIdentifier(user.getIdentifier()).isPresent()) {
            //throw new ResponseStatusException(HttpStatus.CONFLICT, "This user is already in the system");
            throw new UserExistsException("This user is already in the system");
        }
        return userRepository.save(user);
    }

    public User updateUser(int id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDetails.getName());
            user.setAge(userDetails.getAge());
            return userRepository.save(user);
        }).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}
