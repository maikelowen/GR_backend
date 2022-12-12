package com.projects.casinApp_v3.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.projects.casinApp_v3.model.User;
import com.projects.casinApp_v3.service.GRUserImpl;
import com.projects.casinApp_v3.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/gr")
@CrossOrigin
public class GoldenRaceUserController {

    @Autowired
    GRUserImpl grUser;

    @GetMapping("/get-url")
    @ResponseBody
    public String getURL(@RequestParam String entityName) throws JsonProcessingException {
        return grUser.getURL(entityName);
    }

    @GetMapping("/find")
    @ResponseBody
    public boolean findUser(@RequestParam int extId) throws JsonProcessingException {
        return grUser.checkUser(extId);
    }

    @PostMapping ("/add")
    @ResponseBody
    public ResponseEntity<String> addUser(@RequestParam int parentId, @RequestParam String entityName, @RequestParam int extId ) throws JsonProcessingException {
        return grUser.addUser(parentId, entityName, extId);
    }

    @PostMapping ("/login")
    @ResponseBody
    public ResponseEntity<String> loginUser(@RequestParam int playerId ) throws JsonProcessingException {
        return grUser.loginUser(playerId);
    }

}
