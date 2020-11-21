//
package com.smart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.smart.model.User;
import com.smart.repository.UserRepository;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.smart.helper.Message;
import javax.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Controller
public class HomeController {
  
        @Autowired
        private BCryptPasswordEncoder passEncoder;
    
    
       @Autowired
       UserRepository repo;
    
    @GetMapping("/")
    public String home(Model model){
    
      model.addAttribute("title","Home - Smart Contact Manager");
        
       return "home";
    }
      @GetMapping("/about")
    public String about(Model model){
    
      model.addAttribute("title","About - Smart Contact Manager");
        
       return "about";
    }
       @GetMapping("/register")
    public String register(Model model){
    
      model.addAttribute("title","register - Smart Contact Manager");
      model.addAttribute("user",new User());
        
       return "register";
    }
    
    
    
    // handler for registration 
    
    @PostMapping("/process")
    public String registraionHandler(@Valid @ModelAttribute("user") User user,BindingResult result, 
            @RequestParam(value = "agreement",defaultValue = "false") boolean agreement,Model model,HttpSession session) {

        try {
            if(!agreement){
               System.out.println("You have not accepted terms and conditions");
               throw new Exception("You have not accepted terms and conditions");
           }
            
            if(result.hasErrors()){
            
                model.addAttribute("user",user);
                return "register";
            
            }
        
        
           user.setRoles("ROLE_USER");
           user.setEnable(true);
           user.setImageUrl("default.png");
           user.setPassword(passEncoder.encode(user.getPassword()));
           System.out.println("agreement " + agreement);
           System.out.println("user " + user);
            
            repo.save(user);
          
           model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Succefully Registered !!", "alert-success"));
     
        return "register";
        } catch (Exception e) {
            
            model.addAttribute("user",user);
            session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
            
            return "register";
        }
        
           
    }


    //handler for login 

    @GetMapping("/logins")
    public String customLogin(Model model){
    
      model.addAttribute("title","login - Smart Contact Manager");
      model.addAttribute("user",new User());
        
       return "login";
    }

}

