package com.nayak.Notes_APP.repo;

import com.nayak.Notes_APP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findByVerificationCode(String code);
}
