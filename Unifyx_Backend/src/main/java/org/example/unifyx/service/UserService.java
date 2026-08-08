package org.example.unifyx.service;

import org.example.unifyx.Model.Users;
import org.example.unifyx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public String getUserRoleById(String uid) {
        return userRepository.findRoleByUid(uid);
    }


//    public Optional<Users> getUserByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }


    public Users createUser(Users user) {
        if (user.getUid() == null || user.getUid().isBlank()) {
            throw new IllegalArgumentException("UID cannot be null or empty");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        if (userRepository.existsByUid(user.getUid())) {
            throw new IllegalArgumentException("UID already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        return userRepository.save(user);
    }

    @Transactional
    public Users updateUser(String uid, Users userDetails) {
        return userRepository.findByUid(uid).map(user -> {
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(String uid) {
        Users user = userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }


    public Users getUserById(String uid) {
        Optional<Users> user = userRepository.findByUid(uid);
        return user.orElse(null);
    }
}
