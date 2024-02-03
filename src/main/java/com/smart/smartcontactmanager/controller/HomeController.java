package com.smart.smartcontactmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@Valid
public class HomeController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String Home(Model m){
        m.addAttribute("title", "Home-Smart Contact Manager");
       
        return "home";
    }
    @GetMapping("/about")
    public String About(Model m){
        m.addAttribute("title", "About-Smart Contact Manager");
        
       
        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title", "Signup-Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }
    //handler for registering
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult results, @RequestParam(value="agreement",defaultValue="false")boolean agreement,Model model,HttpSession session){
        
       try {
        if (!agreement) {
            System.out.println("You have not agreed the terms and conditions");
            throw new Exception("You have not agreed the terms and conditions");
        }
        if (results.hasErrors()) {
            System.out.println("ERROR"+results.toString());
            model.addAttribute("user", user);
            return "signup";
        }
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setImageUrl("default.png");

        User user2=userRepository.save(user);
       model.addAttribute("user", new User());
       session.setAttribute("message", new Message("Succesfully Registered!!", "alert-success"));
        
        return "signup";
       } catch (Exception e) {
        // TODO: handle exception
        e.printStackTrace();
        model.addAttribute("user", user);
        session.setAttribute("message", new Message("something went wrong!","alert-danger"));
        return "signup";


       }
    }
    
}
