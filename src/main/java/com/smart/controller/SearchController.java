
package com.smart.controller;

import com.smart.model.Contacts;
import com.smart.model.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SearchController {
    
    @Autowired
    private ContactRepository contactRepo;
    
    @Autowired
    private UserRepository userRepo;
    
    @GetMapping("/search/{query}")
    public ResponseEntity<?>  SearchContacts(@PathVariable("query") String query,Principal p){
        
        User user = userRepo.getUserByEmail(p.getName());
    
       List<Contacts> contacts= contactRepo.findByNameContainingAndUser(query, user);
       
       return ResponseEntity.ok(contacts);
        
    }
    
}
