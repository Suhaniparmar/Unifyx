package org.example.unifyx.Service;

import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.Repository.WorkerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkerProfileService {
    @Autowired
    private WorkerProfileRepository workerProfileRepository;


    public void addWorkerProfile(WorkerProfile workerProfile) {
        workerProfileRepository.save(workerProfile);
    }
}
