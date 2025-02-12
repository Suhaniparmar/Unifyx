package org.example.unifyx.Controller;

import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.Repository.WorkerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workerprofiles")
public class WorkerProfileController {

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    // Create a new worker profile
    @PostMapping
    public ResponseEntity<WorkerProfile> createWorkerProfile(@RequestBody WorkerProfile workerProfile) {
        WorkerProfile savedProfile = workerProfileRepository.save(workerProfile);
        return new ResponseEntity<>(savedProfile, HttpStatus.CREATED);
    }

    // Get all worker profiles
    @GetMapping
    public List<WorkerProfile> getAllWorkerProfiles() {
        return workerProfileRepository.findAll();
    }

    // Get a worker profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<WorkerProfile> getWorkerProfileById(@PathVariable int id) {
        Optional<WorkerProfile> profile = workerProfileRepository.findById(id);
        return profile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Update an existing worker profile
    @PutMapping("/{id}")
    public ResponseEntity<WorkerProfile> updateWorkerProfile(@PathVariable int id, @RequestBody WorkerProfile workerProfile) {
        if (workerProfileRepository.existsById(id)) {
            workerProfile.setWorkerId(id);
            WorkerProfile updatedProfile = workerProfileRepository.save(workerProfile);
            return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete a worker profile
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkerProfile(@PathVariable int id) {
        if (workerProfileRepository.existsById(id)) {
            workerProfileRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
