package com.nayak.Notes_APP.service;

import com.nayak.Notes_APP.entity.User;
import com.nayak.Notes_APP.repo.UserRepo;
import jakarta.mail.internet.MimeMessage;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public User createUser(User user,String url) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");

        user.setEnabled(false);
        RandomString rn = new RandomString();
        user.setVerificationCode(rn.make(64)); //64 bit

        User u=userRepo.save(user);
        sendVerificationMail(user,url);

        return u;
    }

    @Override
    public boolean checkByEmail(String email) {

        return userRepo.existsByEmail(email);
    }

    @Override
    public User findByEmail(String email) {

        return userRepo.findByEmail(email);
    }

    public boolean authenticate(String email, String password) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            // Compare the raw password with the hashed password stored in the database
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    public void sendVerificationMail(User user,String url){
        String from = "thecodehelper534@gmail.com";
        String to = user.getEmail();
        String sub = "Account verification";
        String content="Dear [[name]],<br>"
                + "Please click the link below to verify your registration: <br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you, <br>"
                + "Notes-App";

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(from,"Notes-App");
            helper.setTo(to);
            helper.setSubject(sub);

            content = content.replace("[[name]]",user.getUsername());

            String siteUrl = url + "/api/verify?code=" + user.getVerificationCode();

            content = content.replace("[[URL]]",siteUrl);

            helper.setText(content,true);

            mailSender.send(message);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean checkByVerificationCode(String code) {
        User user = userRepo.findByVerificationCode(code);

        if(user!=null){
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepo.save(user);
            return true;
        }
        return false;
    }


}
