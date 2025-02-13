package org.example.unifyx.Service;


import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.Repository.OwnerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerProfileService {

    @Autowired
    private OwnerProfileRepository ownerProfileRepository;


    public void addOwnerProfile(OwnerProfile ownerProfile) {
        ownerProfileRepository.save(ownerProfile);
    }
}
