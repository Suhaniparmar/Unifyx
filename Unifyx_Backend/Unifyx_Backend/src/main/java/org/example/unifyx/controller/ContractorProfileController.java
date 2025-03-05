package org.example.unifyx.controller;

import org.example.unifyx.Model.ContractorProfile;
import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.service.ContractorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contractor")
public class ContractorProfileController {

    @Autowired
    private ContractorProfileService contractorProfileService;

    // Create a new contractor profile
    @PostMapping
    public ResponseEntity<ContractorProfile> createContractorProfile(@RequestBody ContractorProfile contractorProfile) {

        ContractorProfile savedContractor = contractorProfileService.addContractorProfile(contractorProfile);
        return new ResponseEntity<>(savedContractor, HttpStatus.CREATED);
    }

}
