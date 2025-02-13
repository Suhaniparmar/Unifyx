package org.example.unifyx.Service;

import org.example.unifyx.Model.ContractorProfile;
import org.example.unifyx.Repository.ContractorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractorProfileService {

    @Autowired
    private ContractorProfileRepository contractorProfileRepository;

    public void addContractorProfile(ContractorProfile contractorProfile) {
        contractorProfileRepository.save(contractorProfile);

    }
}
