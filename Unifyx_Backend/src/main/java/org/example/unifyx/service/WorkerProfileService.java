package org.example.unifyx.service;

import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.repository.WorkerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WorkerProfileService {
    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    public WorkerProfile addWorkerProfile(WorkerProfile workerProfile) {
        Optional<WorkerProfile> existing = workerProfileRepository
                .findTopByEmailIgnoreCaseOrderByWorkerIdDesc(workerProfile.getEmail());
        existing.ifPresent(profile -> workerProfile.setWorkerId(profile.getWorkerId()));

        WorkerProfile savedWorkerProfile = workerProfileRepository.save(workerProfile);
        System.out.println("Saved worker ID: " + savedWorkerProfile.getWorkerId());
        return savedWorkerProfile;
    }

    public WorkerProfile getWorkerByEmail(String email) {
        return workerProfileRepository.findTopByEmailIgnoreCaseOrderByWorkerIdDesc(email).orElse(null);
    }
}
