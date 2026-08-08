package org.example.unifyx.controller;

import org.example.unifyx.Model.ContractorProfile;
import org.example.unifyx.service.ContractorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contractor")
public class ContractorProfileController {

    @Autowired
    private ContractorProfileService contractorProfileService;

    // Create a new contractor profile
    @PostMapping
    public ResponseEntity<ContractorProfile> createContractorProfile(@RequestBody ContractorProfile contractorProfile) {
        if (!StringUtils.hasText(contractorProfile.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        ContractorProfile savedContractor = contractorProfileService.addContractorProfile(contractorProfile);
        return new ResponseEntity<>(savedContractor, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<ContractorProfile> getContractorProfile(@RequestParam String email) {
        if (!StringUtils.hasText(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        ContractorProfile contractor = contractorProfileService.getContractorByEmail(email);
        if (contractor != null) {
            return ResponseEntity.ok(contractor);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

}
