package org.example.unifyx.controller;

import org.example.unifyx.Model.OwnerProfile;
import org.example.unifyx.service.OwnerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owner")
public class OwnerProfileController {

    @Autowired
    private OwnerProfileService ownerProfileService;

    // Create a new owner profile
    @PostMapping
    public ResponseEntity<OwnerProfile> createOwnerProfile(@RequestBody OwnerProfile ownerProfile) {
        OwnerProfile savedOwner = ownerProfileService.addOwnerProfile(ownerProfile);
        return new ResponseEntity<>(savedOwner, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<OwnerProfile> getOwnerProfile(@RequestParam String email) {
        OwnerProfile owner = ownerProfileService.getOwnerByEmail(email);
        if(owner != null) {
            return ResponseEntity.ok(owner);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



}
