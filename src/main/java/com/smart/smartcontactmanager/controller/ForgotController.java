package com.smart.smartcontactmanager.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.service.MailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

    @Autowired
    private MailService mailService;
    @Autowired
    private UserRepository userRepository;
    
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";

    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email,HttpSession session){
       System.out.println("EMAIL "+email);
       Random random= new Random(1000);
       int otp=random.nextInt(9999);
       System.out.println("****************************************************");
       System.out.println("OTP "+otp);
       try {
        
        mailService.sendMail(email, "testing", "OTP :"+otp);
        session.setAttribute("otp", otp);
        session.setAttribute("email", email);
        return "verify_otp";

       } catch (Exception e) {
        e.printStackTrace();
        return "forgot_email_form";
       }
       

    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp,HttpSession session){
        int myotp=(int)session.getAttribute("otp");
        String email=(String)session.getAttribute("email");

        if(myotp==otp){

          User user=  this.userRepository.getUserByUserName(email);
          
          if(user==null){
            //send error message
            //user doesnt exist
            return "forgot_email_form";
          }
          else{
            //send change password form
          }
            return "password_change_form";
        }
        else{
            return "forgot_email_form";
        }


    }
}
