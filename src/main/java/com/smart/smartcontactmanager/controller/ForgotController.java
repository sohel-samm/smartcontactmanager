package com.smart.smartcontactmanager.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanager.service.MailService;

@Controller
public class ForgotController {

    @Autowired
    private MailService mailService;
    
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";

    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email){
       System.out.println("EMAIL "+email);
       Random random= new Random(1000);
       int otp=random.nextInt(9999);
       System.out.println("****************************************************");
       System.out.println("OTP "+otp);
       mailService.sendMail(email, "testing", "OTP :"+otp);
        return "verify_otp";
    }
}
