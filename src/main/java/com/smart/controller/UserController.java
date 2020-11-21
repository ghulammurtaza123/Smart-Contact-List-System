package com.smart.controller;

import com.smart.model.User;
import com.smart.model.Contacts;
import com.smart.repository.UserRepository;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    ContactRepository contact_repo;

    @Autowired
    UserRepository repo;

    @ModelAttribute
    public void helperForRepeatingCode(Model m, Principal p) {

        User user = repo.getUserByEmail(p.getName());
        m.addAttribute("user", user);

    }

    @GetMapping("/index")
    public String dashboard(Model model, Principal princip) {

        model.addAttribute("title", " ⛪  Home");

        return "user/user_index";
    }

    //opening add contact form
    @GetMapping("/add_contacts")
    public String addContacts(Model model) {

        model.addAttribute("title", " ✏  Add Contacts   ");
        model.addAttribute("contact", new Contacts());
        return "user/add_contacts";

    }

//        proceesing contatc form
    @PostMapping("/process-contact")
    public String postContact(@ModelAttribute("contact") Contacts contact, @RequestParam("profileImage") MultipartFile file, HttpSession session, Principal p) {

        try {
            User user = repo.getUserByEmail(p.getName());

            if (file.isEmpty()) {
                System.out.println("No image uploaded!");
                contact.setImage("blank.png");
            } else {
                contact.setImage(file.getOriginalFilename());
                File fileObject = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(fileObject.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            }

            user.getContacts().add(contact);
            contact.setUser(user);
            repo.save(user);
            session.setAttribute("message", new Message("User contact added! Add more..", "success"));

        } catch (Exception e) {
            System.out.println("Error :" + e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong", "danger"));
        }

        return "user/add_contacts";
    }

//       Show contacts handler
    @GetMapping("/show_contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal p) {

        model.addAttribute("title", " 🕵️‍♀️  Show Contacts   ");
        User user = repo.getUserByEmail(p.getName());

        Pageable pageable = PageRequest.of(page, 2);
        Page<Contacts> contact = contact_repo.findContactsByUser(user.getId(), pageable);

        model.addAttribute("contacts", contact);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contact.getTotalPages());
        return "user/show_contacts";
    }

//    showing specific contact details
    @GetMapping("/{cId}/contact")
    public String getspecificContatct(@PathVariable("cId") Integer contactId, Model model, Principal p) {

        Contacts contact = contact_repo.findById(contactId).get();

        String userName = p.getName();

        User user = repo.getUserByEmail(userName);
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contactDetails", contact);
            model.addAttribute("title", contact.getName());
        }

        return "user/contact_details";
    }

//    delete contact handler
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Principal p) {

        Contacts contact = contact_repo.findById(cId).get();

        String userName = p.getName();

        User user = repo.getUserByEmail(userName);
        if (user.getId() == contact.getUser().getId()) {
            contact_repo.deleteById(cId);
        }
        return "redirect:/user/show_contacts/0";
    }

//    open update form handler
    @GetMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cId, Model m, Principal p) {

        
        Contacts contact = contact_repo.findById(cId).get();
        String userName = p.getName();

        User user = repo.getUserByEmail(userName);
        if (user.getId() == contact.getUser().getId()) {

            m.addAttribute("title", "update-contact");
            m.addAttribute("contact", contact);

        }

        return "user/update_form";
    }
    
//    update contact
    
    @PostMapping("/process-update")
    public String updateContact(@ModelAttribute("contact") Contacts contact, @RequestParam("profileImage") MultipartFile file, HttpSession session, Principal p) {

        try {
            User user = repo.getUserByEmail(p.getName());
             Contacts oldContact=  contact_repo.findById(contact.getcId()).get();
            if (file.isEmpty()) {
               
                contact.setImage(oldContact.getImage());
            } else {
                
//                delete old image
                   File oldFile = new ClassPathResource("static/img").getFile();
                   File file1=new File(oldFile,oldContact.getImage());
                   file1.delete();

//                Update image

                contact.setImage(file.getOriginalFilename());
                File fileObject = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(fileObject.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
               

            }

            //user.getContacts().add(contact);
            contact.setUser(user);
            contact_repo.save(contact);
            session.setAttribute("message", new Message("Contact is updated!", "success"));

        } catch (Exception e) {
            System.out.println("Error :" + e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong", "danger"));
        }

        return "redirect:/user/"+contact.getcId()+"/contact";
    }
    
//    user profile handler
    
    @GetMapping("/profile")
    public String userProfile(Model m){
    
        m.addAttribute("title", "Profile Page");
    
        return "user/profile";
    
    }
    

}
