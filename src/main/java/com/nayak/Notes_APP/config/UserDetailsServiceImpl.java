package com.nayak.Notes_APP.config;

import com.nayak.Notes_APP.entity.User;
import com.nayak.Notes_APP.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepo userRepo;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u1 = userRepo.findByEmail(email);

        if(u1!= null){
            return new CustomUserDetails(u1);
        }

        throw new UsernameNotFoundException("User not available.....");
    }
}
