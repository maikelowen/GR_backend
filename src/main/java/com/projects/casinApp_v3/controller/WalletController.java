package com.projects.casinApp_v3.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.projects.casinApp_v3.service.GRWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
@CrossOrigin
public class WalletController {

    @Autowired
    GRWalletService grWalletService;

    @GetMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createWallet(@RequestParam Long grId, @RequestParam int extId) throws JsonProcessingException {

        return grWalletService.checkWallet(grId,extId);
    }

    @PostMapping("/add-credit")
    @ResponseBody
    public ResponseEntity<String> addCredit(@RequestParam BigDecimal credit, @RequestParam String username) throws JsonProcessingException {

        return grWalletService.addCredit(credit, username);
    }
}
