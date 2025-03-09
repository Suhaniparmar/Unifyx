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
        return workerProfileRepository.save(workerProfile);
    }

    public WorkerProfile getWorkerByEmail(String email) {
        return workerProfileRepository.findByEmail(email);
    }
}
