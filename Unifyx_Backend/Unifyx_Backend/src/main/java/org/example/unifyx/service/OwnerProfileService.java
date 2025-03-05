package org.example.unifyx.service;


import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.repository.OwnerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerProfileService {

    @Autowired
    private OwnerProfileRepository ownerProfileRepository;


    public OwnerProfile addOwnerProfile(OwnerProfile ownerProfile) {

        System.out.println("Saving owner: " + ownerProfile);
        return ownerProfileRepository.save(ownerProfile);
    }
}
