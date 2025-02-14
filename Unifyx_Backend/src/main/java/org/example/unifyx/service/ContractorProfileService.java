package org.example.unifyx.service;

import org.example.unifyx.Model.ContractorProfile;
import org.example.unifyx.repository.ContractorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractorProfileService {

    @Autowired
    private ContractorProfileRepository contractorProfileRepository;

    public ContractorProfile addContractorProfile(ContractorProfile contractorProfile) {
        contractorProfileRepository.save(contractorProfile);
        return contractorProfile;
    }
}
