package org.example.unifyx.controller;

import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.service.WorkerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/worker")
public class WorkerProfileController {

    @Autowired
    private WorkerProfileService workerProfileService;

    // Create a new worker profile
    @PostMapping
    public ResponseEntity<WorkerProfile> createWorkerProfile(@RequestBody WorkerProfile workerProfile) {
        if (!StringUtils.hasText(workerProfile.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        WorkerProfile savedWorker = workerProfileService.addWorkerProfile(workerProfile);
        return new ResponseEntity<>(savedWorker, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<WorkerProfile> getWorkerProfile(@RequestParam String email) {
        if (!StringUtils.hasText(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        WorkerProfile worker = workerProfileService.getWorkerByEmail(email);
        if(worker != null) {
            return ResponseEntity.ok(worker);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }




}
