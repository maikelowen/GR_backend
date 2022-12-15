package com.projects.casinApp_v3.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.projects.casinApp_v3.service.GRUserImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gr")
@CrossOrigin
public class GoldenRaceUserController {

    @Autowired
    GRUserImpl grUser;

    //Endpoint that receive an entityName and returns onlineHash
    @GetMapping("/get-url")
    @ResponseBody
    public String getURL(@RequestParam String entityName) throws JsonProcessingException {
        return grUser.getOnlineHash(entityName);
    }

    //Endpoints created only for testing services. Not needed for the application.

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
