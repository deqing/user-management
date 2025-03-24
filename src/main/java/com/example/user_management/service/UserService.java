package com.example.user_management.service;

import com.example.user_management.exception.UserExistsException;
import com.example.user_management.exception.UserNotFoundException;
import com.example.user_management.model.User;
import com.example.user_management.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static final String REDIS_KEY = "users";

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserService(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public List<User> getAllUsers() {
        List<User> cachedUsers = (List<User>) redisTemplate.opsForValue().get(REDIS_KEY);
        if (cachedUsers != null) {
            return cachedUsers;
        }

        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            redisTemplate.opsForValue().set(REDIS_KEY, users);
        }
        return users;
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
        redisTemplate.delete(REDIS_KEY);
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
