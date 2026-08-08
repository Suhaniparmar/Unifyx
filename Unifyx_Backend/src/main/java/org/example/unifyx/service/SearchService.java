package org.example.unifyx.service;

import org.example.unifyx.Model.ContractorProfile;
import org.example.unifyx.Model.Post;
import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.repository.ContractorProfileRepository;
import org.example.unifyx.repository.PostRepository;
import org.example.unifyx.repository.WorkerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @Autowired
    private ContractorProfileRepository contractorProfileRepository;

    public List<WorkerProfile> getWorkersByLocation(String location) {
        return workerProfileRepository.findByAddressIgnoreCase(location);
    }

    public List<ContractorProfile> getContractorsByLocation(String location) {
        return contractorProfileRepository.findByAddressIgnoreCase(location);
    }



    public List<Post> searchPostsByLocation(String location) {
        return postRepository.findBySiteLocation(location);
    }

}
