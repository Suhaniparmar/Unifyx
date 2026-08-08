package org.example.unifyx.repository;


import org.example.unifyx.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("SELECT u.role FROM Users u WHERE u.uid = :uid")
    String findRoleByUid(@Param("uid") String uid);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUid(String uid);

    boolean existsByUid(String uid);

    void deleteByUid(String uid);

    Users findUserByUid(String uid);
}


