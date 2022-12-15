package com.projects.casinApp_v3.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.projects.casinApp_v3.dto.CheckWalletResponse;
import com.projects.casinApp_v3.dto.CreateWalletResponse;
import com.projects.casinApp_v3.model.GRUser;
import com.projects.casinApp_v3.model.GRWallet;
import com.projects.casinApp_v3.repository.GRUserRepository;
import com.projects.casinApp_v3.repository.GRWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class GRWalletService {

    private String entityName;
    private Long grId;
    private Long gr_wallet_id;
    private int extId;

    private final String apiId = "49985";
    private final String apiHash = "7ab19f172abe51a34c2bfa4f313ccf2e";
    private final String apiDomain = "Test";
    @Autowired
    private GRWalletRepository grWalletRepository;
    @Autowired
    private GRUserRepository grUserRepository;

    //Method that receive credit and username and returns http response
    public ResponseEntity<String> addCredit(BigDecimal credit, String username) {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        try {
            System.out.println("Trying to find Wallet from username: "+ username);
            //Checking user's wallet in DB
            findByUsername(username);
            //Checking user's wallet in GR
            if(checkWallet(grId, extId).getStatusCode()== HttpStatus.OK){
                System.out.println("User exists in GR USER table");
                //Adding Headers
                HttpEntity<Object> entity = addHeaders();
                //Initialize RestTemplate object
                RestTemplate restTemplate = new RestTemplate();
                //Setting URL
                String url = "https://api-int.virtustec.com:8383/api/external/v2/wallet/credit/add?walletId="+gr_wallet_id+"&currencyCode=EUR&amount="+credit;
                //Send request and keep response
                response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                System.out.println("Credit Added");
            }else{
                System.out.println("User does not exist");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    // Method that check if wallet exists in GR System. Receive grId and extId and returns response.
    public ResponseEntity<String> checkWallet(Long grId, int extId) throws JsonProcessingException {
        this.grId = grId;
        this.extId = extId;
        System.out.println("GR User ID: "+ grId);
        System.out.println("ExtID: "+ extId);
        //Adding Headers
        HttpEntity<Object> entity = addHeaders();
        //Setting URL
        String url = "https://api-int.virtustec.com:8383/api/external/v2/wallet/findById?entityId=" + grId + "&extId=" + extId + "&withChildren=false";
        //Initialize RestTemplate object
        RestTemplate restTemplate = new RestTemplate();
        //Send request and keep response
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        //If wallet exists in GR
        if(response.getStatusCode() == HttpStatus.OK) {
            //Initialize ObjectMapper object
            ObjectMapper om = new ObjectMapper();
            //Mapping response to walletResponses DTO
            CheckWalletResponse[] walletResponses = om.readValue(response.getBody(), CheckWalletResponse[].class);
            System.out.println("Request Successful!");
            System.out.println(response.getBody());
            //Setting gr_wallet_id to response wallet's ID value
            gr_wallet_id = Long.valueOf(Arrays.stream(walletResponses).mapToInt(CheckWalletResponse::getId).sum());
            System.out.println("GR Wallet ID: "+ gr_wallet_id);
            //Checking if that wallet's ID exists DB
            boolean exists = walletExist(gr_wallet_id);
            //If it doesn't exist create a new wallet and save it in DB
            if (!exists) {
                createWalletDB(gr_wallet_id,grId, extId);
                }
            //If it exists do nothing
            if (exists){
                System.out.println("Wallet ID exists in DB");
                }
        }
        //If wallet does not exist in GR
        if(response.getStatusCode() == HttpStatus.NO_CONTENT) {
            System.out.println("No hay wallet para ese usuario");
            //Create wallet
            createWallet(grId, extId);
        }

        return response;
    }


    // Method to create wallet in GR System. Receive grId and extId and returns response.
    public ResponseEntity <String>createWallet(Long grId, int extId) throws JsonProcessingException {
        //Initialize credit to 0. Only to create the wallet.
        BigDecimal credit = BigDecimal.valueOf(0);
        //Initialize currency to EUR
        String currency = "EUR";
        //Adding Headers
        HttpEntity<Object> entity = addHeaders();
        //Setting the URL
        String url = "https://api-int.virtustec.com:8383/api/external/v2/wallet/create?entityId="+grId+"&extId="+extId+"&currency="+currency+"&balance="+credit;
        //Initiating RestTemplate object
        RestTemplate restTemplate = new RestTemplate();
        //Send Request and keep the response
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        //Initiating ObjectMapper object
        ObjectMapper om = new ObjectMapper();
        //Mapping values from response to CreateWalletResponse DTO
        CreateWalletResponse createWalletResponse = om.readValue(response.getBody(), CreateWalletResponse.class);
        // If response is OK...
        if(response.getStatusCode() == HttpStatus.OK){
            System.out.println("Wallet created in GR");
            //Set wallet's ID
            gr_wallet_id = Long.valueOf(createWalletResponse.getId());
            System.out.println("Wallet ID: " + gr_wallet_id);
            //Check if wallet exists in DB
            boolean exists = walletExist(gr_wallet_id);
            //If it does not...
            if(!exists){
                //Create wallet in DB
                createWalletDB(gr_wallet_id,grId, extId);
            }
            if(exists){
                //Do nothing
                System.out.println("Usuario ya existia en DB");
            }

        }
        return response;
    }


    //Helper Functions

    //Check if wallet exists in DB by gr_wallet_id. Returns boolean.
    public boolean walletExist (Long gr_wallet_id){
        return grWalletRepository.existsBygrWalletId( gr_wallet_id);
    }

    //Create wallet in DB
    public void createWalletDB(Long gr_wallet_id, Long grId, int extId){
        GRWallet grWalletInstance = new GRWallet();
        grWalletInstance.setGrWalletId(gr_wallet_id);
        grWalletInstance.setGrID(grId);
        grWalletInstance.setExtId(extId);
        grWalletRepository.save(grWalletInstance);
        System.out.println("User's Wallet created in DB");
    }

    //Check if wallet exists in DB by username. Returns GRUser object.
    public GRUser findByUsername(String username){
        GRUser grUser = new GRUser();
        grUser = grUserRepository.findByUsername(username).get();
        //System.out.println(grUser);
        grId = grUser.getGrId();
        extId = grUser.getExtId();
        entityName = grUser.getUsername();
        return grUser;
    }

    //Add Headers
    public HttpEntity addHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("apiId", apiId);
        headers.add("apiHash", apiHash);
        headers.add("apiDomain", apiDomain);
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        return entity;
    }

}
