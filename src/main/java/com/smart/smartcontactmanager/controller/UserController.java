package com.smart.smartcontactmanager.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    //method to adding commomn data to response
    @ModelAttribute
    public void addCommonData(Model model,Principal principal){
        String userName=principal.getName();
        System.out.println("USERNAME"+userName);

        User user=userRepository.getUserByUserName(userName);
        System.out.println("USER "+user);
        model.addAttribute("user", user);
          
    }

    @RequestMapping("/index")
    public String dashboard(Model model,Principal principal){
        // String userName=principal.getName();
        // System.out.println("USERNAME"+userName);

        // User user=userRepository.getUserByUserName(userName);
        // System.out.println("USER "+user);
        // model.addAttribute("user", user);
        // 
        return "normal/user_dashboard";
    }
    @GetMapping("/add_contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact";
    }

    @PostMapping("/process_contact")
    public String processContact(@ModelAttribute Contact contact) {

        System.out.println("Data "+contact);
        return "normal/add_contact";
        //TODO: process POST rModelAttribute Contact contact;
    }
    
    


    
}
