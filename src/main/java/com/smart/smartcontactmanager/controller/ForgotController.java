package com.smart.smartcontactmanager.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    @Autowired
    private BCryptPasswordEncoder bcrypt;
    
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";

    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email,HttpSession session,Model model){
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
        model.addAttribute("message", "OTP sent failed!! Try again");
        return "forgot_email_form";
       }
       

    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp,HttpSession session,Model model){
        int myotp=(int)session.getAttribute("otp");
        String email=(String)session.getAttribute("email");

        if(myotp==otp){

          User user=  this.userRepository.getUserByUserName(email);
          
          if(user==null){
            //send error message
            //user doesnt exist
            model.addAttribute("message", "User Doesnt exist!!!");

            return "forgot_email_form";
          }
          else{
            //send change password form
          }
            return "password_change_form";
        }
        else{
            model.addAttribute("message", "OTP Incorrect!!!");

            return "forgot_email_form";
        }


    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session,Model model){

        String email=(String)session.getAttribute("email");
        User user=this.userRepository.getUserByUserName(email);
        user.setPassword(this.bcrypt.encode(newpassword));
        this.userRepository.save(user);
        model.addAttribute("message", "Password changed succesfully");
        return "login";

    }
}
