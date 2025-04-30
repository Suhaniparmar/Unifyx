package org.example.unifyx.service;

import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.repository.WorkerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkerProfileService {
    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    public WorkerProfile addWorkerProfile(WorkerProfile workerProfile) {
        WorkerProfile savedWorkerProfile = workerProfileRepository.save(workerProfile);
        System.out.println("Saved worker ID: " + savedWorkerProfile.getWorkerId());
        return savedWorkerProfile;
    }

    public WorkerProfile getWorkerByEmail(String email) {
        return workerProfileRepository.findByEmail(email);
    }
}
