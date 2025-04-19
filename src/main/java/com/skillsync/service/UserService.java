package com.skillsync.service;

import com.skillsync.entity.User;
import com.skillsync.exception.UserNotFoundException;
import com.skillsync.exception.InvalidUserDataException;
import com.skillsync.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // READ ALL
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // READ BY ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // UPDATE
    public User updateUser(String id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(updatedUser.getEmail());
                    user.setName(updatedUser.getName());
                    user.setPassword(updatedUser.getPassword());
                    user.setFollowers(updatedUser.getFollowers());
                    user.setFollowing(updatedUser.getFollowing());
                    user.setLearningPlanIds(updatedUser.getLearningPlanIds());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // DELETE
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
