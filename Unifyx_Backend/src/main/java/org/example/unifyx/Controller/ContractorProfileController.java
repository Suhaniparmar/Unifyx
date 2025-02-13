package org.example.unifyx.Controller;

import org.example.unifyx.Model.ContractorProfile;
import org.example.unifyx.Repository.ContractorProfileRepository;
import org.example.unifyx.Service.ContractorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contractorprofile")
public class ContractorProfileController {

    @Autowired
    private ContractorProfileRepository contractorProfileRepository;

    @Autowired
    private ContractorProfileService contractorProfileService;

    // Create a new contractor profile
    @PostMapping
    public ResponseEntity<ContractorProfile> createContractorProfile(@RequestBody ContractorProfile contractorProfile) {

         contractorProfileService.addContractorProfile(contractorProfile);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Get all contractor profiles
    @GetMapping("/api/contractorprofiles/all")
    public List<ContractorProfile> getAllContractorProfiles() {
        return contractorProfileRepository.findAll();
    }

    // Get a contractor profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<ContractorProfile> getContractorProfileById(@PathVariable int id) {
        Optional<ContractorProfile> profile = contractorProfileRepository.findById(id);
        return profile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Update an existing contractor profile
    @PutMapping("/{id}")
    public ResponseEntity<ContractorProfile> updateContractorProfile(@PathVariable int id, @RequestBody ContractorProfile contractorProfile) {
        if (contractorProfileRepository.existsById(id)) {
            contractorProfile.setContractorId(id);
            ContractorProfile updatedProfile = contractorProfileRepository.save(contractorProfile);
            return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete a contractor profile
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContractorProfile(@PathVariable int id) {
        if (contractorProfileRepository.existsById(id)) {
            contractorProfileRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
