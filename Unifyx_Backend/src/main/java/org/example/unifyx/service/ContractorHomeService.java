package org.example.unifyx.service;

import org.example.unifyx.Model.ContractorProfile;
import org.example.unifyx.Model.Post;
import org.example.unifyx.Model.WorkerProfile;
import org.example.unifyx.repository.ContractorProfileRepository;
import org.example.unifyx.repository.WorkerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContractorHomeService {
    @Autowired
    private ContractorProfileRepository contractorProfileRepository;

    @Autowired
    private PostService postService;

    /**
     * Fetch posts that match the worker's categories
     */
    public List<Post> getMatchingPostsForContractor(int contractorId) {
        ContractorProfile contractor = contractorProfileRepository.findById(contractorId).orElse(null);

        if (contractor== null || contractor.getCategories().isEmpty()) {
            return List.of(); // Return empty list if no worker or categories exist
        }

        return postService.getAllPosts().stream()
                .filter(post -> contractor.getCategories().contains(post.getWorkerCategory()))
                .collect(Collectors.toList());
    }
}
