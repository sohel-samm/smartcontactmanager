package com.smart.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
                contact.setImage("contact.png");
                
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

    //per page=5[n]
    //current page=0[page]
    @GetMapping("/show-contacts/{page}")   
    public String showContacts(@PathVariable("page") Integer page, Model model,Principal principal) {
        model.addAttribute("title", "Show Contacts");

            String userName=principal.getName();
            User user=this.userRepository.getUserByUserName(userName);

            Pageable pageable=PageRequest.of(page,5);
            Page<Contact> contacts=this.contactRepository.findContactByUser(user.getId(),pageable);

                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", contacts.getTotalPages());

                model.addAttribute("contacts", contacts);
        return "normal/show_contacts";
    }
    

    // Handler to show contcat detail
    @GetMapping("/{cid}/contact")
    public String showContactDetail(@PathVariable("cid") Integer cid,Model model,Principal principal){
        System.out.println("CID"+cid);
        Optional<Contact> contactoptional=this.contactRepository.findById(cid);
        Contact contact=contactoptional.get();

        String username=principal.getName();
        User u=this.userRepository.getUserByUserName(username);
        if(u.getId()==contact.getUser().getId())
        model.addAttribute("contact",contact);

        return "normal/contact_detail";
    }

    //Handler to delete contact
    @GetMapping("/delete/{cid}")    
    public String deleteContact(@PathVariable("cid") Integer cid,Model model) {
       Optional<Contact> contactOptional= this.contactRepository.findById(cid);
        Contact c=contactOptional.get();
        c.setUser(null);
        contactRepository.delete(c);
        // return "normal/user_dashboard";
        model.addAttribute("msg", new Message("Contact Added Successfully!!","alert-success"));

        return "redirect:/user/show-contacts/0";
    }

    //Update contact

    @PostMapping("/update-contact/{cid}")
    public String updateContact(@PathVariable("cid") Integer cid,Model model) {
        
        model.addAttribute("contact", contactRepository.findById(cid).get());
        return "normal/update_form";
    }
    
    //Update form process
    @PostMapping("/update_process_contact")
    public String updateContactProcess(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model model,Principal principal){
        System.out.println("contact name "+contact.getName());
        System.out.println("contact id "+contact.getCid());

            try {
                Contact oldContact=this.contactRepository.findById(contact.getCid()).get();
                if(!file.isEmpty()){
                    //delete old photo
                    File deleteFile=new ClassPathResource("static/img").getFile();
                    File file2=new File(deleteFile,oldContact.getImage());
                    file2.delete();




                    //update profile photo
                    contact.setImage(file.getOriginalFilename());
                    File saveFile=new ClassPathResource("static/img").getFile();
                    Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);


                }
                
                // System.out.println("Image is uploaded");
                // model.addAttribute("msg",new Message("Contact Updated Successfully!!","alert-success"));
                else{
                    contact.setImage(oldContact.getImage());
                }
            User user=this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
                
            } catch (Exception e) {

                e.printStackTrace();
            }

        return "redirect:/user/"+contact.getCid()+"/contact";
    }

    @GetMapping("/profile")
    public String yourProfile(Model model){
        model.addAttribute("title", "Profile page");
        return "normal/profile";
    }
    


    
}
