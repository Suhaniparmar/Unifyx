package org.example.unifyx.controller;

import org.example.unifyx.Model.Users;
import org.example.unifyx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Fetch all users
    @GetMapping
    public List<Users> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody Users user) {
        if (user.getUid() == null || user.getEmail() == null || user.getRole()==null) {
            return ResponseEntity.badRequest().body("UID, Email and role are required");
        }
        try {
            userService.createUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ Fetch a user by UID
    @GetMapping("/{uid}")
    public ResponseEntity<Users> getUserById(@PathVariable String uid) {
        Users user = userService.getUserById(uid);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/role/{uid}")
    public ResponseEntity<Map<String, String>> getUserRoleByUid(@PathVariable String uid) {
        String role = userService.getUserRoleById(uid);
        if (role != null) {
            Map<String, String> response = new HashMap<>();
            response.put("role", role);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
    }



    // ✅ Register a new user using UserService
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Users user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email cannot be null or empty");
        }
        try {
            userService.createUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ Update user details
    @PutMapping("/{uid}")
    public ResponseEntity<Users> updateUser(@PathVariable String uid, @RequestBody Users userDetails) {
        try {
            return ResponseEntity.ok(userService.updateUser(uid, userDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Delete a user
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteUser(@PathVariable String uid) {
        userService.deleteUser(uid);
        return ResponseEntity.noContent().build();
    }
}
