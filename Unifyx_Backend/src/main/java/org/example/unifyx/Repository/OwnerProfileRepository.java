package org.example.unifyx.Repository;

import org.example.unifyx.Model.OwnerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerProfileRepository extends JpaRepository<OwnerProfile, Integer> {
    // You can add custom queries here if necessary
}
