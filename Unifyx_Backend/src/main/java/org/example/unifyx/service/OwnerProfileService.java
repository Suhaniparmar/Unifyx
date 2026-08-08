package org.example.unifyx.service;


import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.repository.OwnerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OwnerProfileService {

    @Autowired
    private OwnerProfileRepository ownerProfileRepository;


    public OwnerProfile addOwnerProfile(OwnerProfile ownerProfile) {
        Optional<OwnerProfile> existing = ownerProfileRepository
                .findTopByEmailIgnoreCaseOrderByOwnerIdDesc(ownerProfile.getEmail());
        existing.ifPresent(profile -> ownerProfile.setOwnerId(profile.getOwnerId()));
        return ownerProfileRepository.save(ownerProfile);
    }

    public OwnerProfile getOwnerByEmail(String email) {
        return ownerProfileRepository.findTopByEmailIgnoreCaseOrderByOwnerIdDesc(email).orElse(null);
    }
}
