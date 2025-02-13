package org.example.unifyx.Controller;

import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.Repository.OwnerProfileRepository;
import org.example.unifyx.Service.OwnerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ownerprofiles")
public class OwnerProfileController {

    @Autowired
    private OwnerProfileRepository ownerProfileRepository;

    @Autowired
    private OwnerProfileService ownerProfileService;

    // Create a new owner profile
    @PostMapping
    public ResponseEntity<OwnerProfile> createOwnerProfile(@RequestBody OwnerProfile ownerProfile) {
        ownerProfileService.addOwnerProfile(ownerProfile);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Get all owner profiles
    @GetMapping
    public List<OwnerProfile> getAllOwnerProfiles() {
        return ownerProfileRepository.findAll();
    }

    // Get an owner profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<OwnerProfile> getOwnerProfileById(@PathVariable int id) {
        Optional<OwnerProfile> profile = ownerProfileRepository.findById(id);
        return profile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Update an existing owner profile
    @PutMapping("/{id}")
    public ResponseEntity<OwnerProfile> updateOwnerProfile(@PathVariable int id, @RequestBody OwnerProfile ownerProfile) {
        if (ownerProfileRepository.existsById(id)) {
            ownerProfile.setOwnerId(id);
            OwnerProfile updatedProfile = ownerProfileRepository.save(ownerProfile);
            return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete an owner profile
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwnerProfile(@PathVariable int id) {
        if (ownerProfileRepository.existsById(id)) {
            ownerProfileRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
