package org.example.unifyx.controller;

import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.service.WorkerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/worker")
public class WorkerProfileController {

    @Autowired
    private WorkerProfileService workerProfileService;

    // Create a new worker profile
    @PostMapping
    public ResponseEntity<WorkerProfile> createWorkerProfile(@RequestBody WorkerProfile workerProfile) {
        WorkerProfile savedWorker = workerProfileService.addWorkerProfile(workerProfile);
        return new ResponseEntity<>(savedWorker, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<WorkerProfile> getWorkerProfile(@RequestParam String email) {
        WorkerProfile worker = workerProfileService.getWorkerByEmail(email);
        if(worker != null) {
            return ResponseEntity.ok(worker);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }




}
