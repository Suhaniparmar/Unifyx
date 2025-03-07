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
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.save(user);
    }

    @Transactional
    public Users updateUser(String uid, Users userDetails) {
        return userRepository.findById(uid).map(user -> {
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(String uid) {
        userRepository.deleteById(uid);
    }


    public Users getUserById(String uid) {
        Optional<Users> user = userRepository.findById(uid);
        return user.orElse(null);
    }
}
