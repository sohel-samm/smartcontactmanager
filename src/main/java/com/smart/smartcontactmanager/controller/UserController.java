package com.smart.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.smartcontactmanager.dao.ContactRepository;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.Contact;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helper.Message;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

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

    //FORM PROCESSING
    @PostMapping("/process_contact")
    public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file, Principal principal,Model model) {

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        user.getContacts().add(contact);
        contact.setUser(user);
        try {
            if (file.isEmpty()) {
                
            } else {
                contact.setImage(file.getOriginalFilename());
                File saveFile=new ClassPathResource("static/img").getFile();
                Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
                model.addAttribute("msg",new Message("Contact Added Successfully!!","alert-success"));
            }


            userRepository.save(user);
        } catch (Exception e) {
            model.addAttribute("msg",new Message("Something Went Wrong!!","alert-danger"));
            e.printStackTrace();
        }
        

        System.out.println("*****************************INSIDE PROCESS CONTACT************************");
        
        System.out.println("Data "+contact);
        System.out.println("addded contact");
        System.out.println("*************************OUTSIDE PROCESS CONTACT*****************************");
        return "normal/add_contact";
        //TODO: process POST rModelAttribute Contact contact;
    }
    
    //SHOW CONTACTS HANDLER

    @GetMapping("/show-contacts")   
    public String showContacts(Model model,Principal principal) {
        model.addAttribute("title", "Show Contacts");

            String userName=principal.getName();
            User user=this.userRepository.getUserByUserName(userName);
            List<Contact> contacts=this.contactRepository.findContactByUser(user.getId());

                model.addAttribute("contacts", contacts);
        return "normal/show_contacts";
    }
    

    


    
}
