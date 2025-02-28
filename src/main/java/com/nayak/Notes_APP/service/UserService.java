package com.nayak.Notes_APP.service;

import com.nayak.Notes_APP.entity.User;


public interface UserService {
    User createUser(User user,String url);

     boolean checkByEmail(String email);

    boolean checkByVerificationCode(String code);

    boolean authenticate(String email, String password);

    User findByEmail(String email);
}
