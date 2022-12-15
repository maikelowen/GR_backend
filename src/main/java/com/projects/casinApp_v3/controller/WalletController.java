package com.projects.casinApp_v3.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.projects.casinApp_v3.service.GRWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
@CrossOrigin
public class WalletController {

    @Autowired
    GRWalletService grWalletService;

    //Endpoint to add credit to GR Wallet. Receive two params: credit and username. Returns https response.
    @PostMapping("/add-credit")
    @ResponseBody
    public ResponseEntity<String> addCredit(@RequestParam BigDecimal credit, @RequestParam String username) throws JsonProcessingException {

        return grWalletService.addCredit(credit, username);
    }

    //Endpoint just for testing. Not within the application flow.
    @GetMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createWallet(@RequestParam Long grId, @RequestParam int extId) throws JsonProcessingException {

        return grWalletService.checkWallet(grId,extId);
    }
}
