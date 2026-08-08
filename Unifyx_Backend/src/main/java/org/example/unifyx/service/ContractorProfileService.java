package org.example.unifyx.service;

import org.example.unifyx.Model.ContractorProfile;
import org.example.unifyx.repository.ContractorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContractorProfileService {

    @Autowired
    private ContractorProfileRepository contractorProfileRepository;

    public ContractorProfile addContractorProfile(ContractorProfile contractorProfile) {
        Optional<ContractorProfile> existing = contractorProfileRepository
                .findTopByEmailIgnoreCaseOrderByContractorIdDesc(contractorProfile.getEmail());
        existing.ifPresent(profile -> contractorProfile.setContractorId(profile.getContractorId()));

        return contractorProfileRepository.save(contractorProfile);
    }

    public ContractorProfile getContractorByEmail(String email) {
        return contractorProfileRepository.findTopByEmailIgnoreCaseOrderByContractorIdDesc(email).orElse(null);
    }
}
