package com.projects.casinApp_v3.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.projects.casinApp_v3.dto.CheckUserResponse;
import com.projects.casinApp_v3.dto.LoginResponse;
import com.projects.casinApp_v3.model.GRUser;
import com.projects.casinApp_v3.model.User;
import com.projects.casinApp_v3.repository.GRUserRepository;
import com.projects.casinApp_v3.repository.GRWalletRepository;
import com.projects.casinApp_v3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GRUserImpl {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GRWalletRepository grWalletRepository;

    @Autowired
    private GRUserRepository grUserRepository;
    @Autowired
    private  GRWalletService grWalletService;

    private final String apiId = "49985";
    private final String apiHash = "7ab19f172abe51a34c2bfa4f313ccf2e";
    private final String apiDomain = "Test";

    private int grId;
    private int extId;
    private String entityName;
    private String onlineHash;
    private int parentId = 49985;
    private boolean existUser = false;
    private boolean addedUser = false;


    //Method that receive param username and returns OnlineHash
    public String getURL(String username) throws JsonProcessingException {

        System.out.println("getURL starts...");

        //Check if USER exists in DB by username.
        try{
            User user = userRepository.findByUsername(username).get();
            entityName = user.getUsername();
            extId = user.getExtId();
            //Testing
            System.out.println(("Username is:" + user.getUsername()));
            System.out.println("ID is:" + user.getId());
            System.out.println("extId is:" +user.getExtId());
        }catch (final Exception e){
            System.out.println("User does not exists in USERS table");
            e.printStackTrace();
        }
        //Check USER exists in GR System
        //If USER exists..
        if(existUser= checkUser(extId)){
            System.out.println("User exist in GR System!");
            try {
                //Call Logging method sending grId param
                loginUser(grId);

            }catch (final Exception e){
                System.out.println("Error LOGGING the user in GR: ");
                e.printStackTrace();
            }
        //If USER does not exist...
        } if(!existUser){
            System.out.println("User does not exist. Les create it in GR System");
            try {
                //Call addUser method sending grId, entityName and extId params
                addUser(parentId, entityName, extId);
                //If addUser is true
                if(addedUser){
                    //Call Logging method sending grId param
                    loginUser(grId);
                }
            }catch (final Exception e){
                e.printStackTrace();
            }
        }
        return  onlineHash;
    }

    //Method that receive an extId and returns true if user exists in GR
    public boolean checkUser (int extId) throws JsonProcessingException {
        boolean isUser = false;

        //Adding headers
        HttpEntity<Object> entity = addHeaders();
        //Setting URL
        String url = "https://api-int.virtustec.com:8383/api/external/v2/entity/findById?extId=" + extId;
        //Initiating RestTemplate object
        RestTemplate restTemplate = new RestTemplate();
        //Send request and keep response
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        //Initiating ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        //If response's body is not null
        if(response.getBody()!=null){
            //Map response to CheckUserResponse DTO
            CheckUserResponse checkUserResponse = mapper.readValue(response.getBody(), CheckUserResponse.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Request Successful.");
                System.out.println(response.getBody());
                isUser = true;
                grId = checkUserResponse.getId();
                parentId = checkUserResponse.getParentId();
                entityName = checkUserResponse.getName();
                setGRUser(entityName, grId, extId);
                try {
                    grWalletService.checkWallet((long) grId, extId);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            else {
                System.out.println("Request Failed");
                System.out.println(response.getStatusCode());
                isUser = false;
            }
        }


        return isUser;
    }

    public ResponseEntity<String> addUser (int parentId, String entityName, int extId) throws JsonProcessingException {
        HttpEntity<Object> entity = addHeaders();

        String url = "https://api-int.virtustec.com:8383/api/external/v2/entity/add?entityParentId="+parentId+"&entityName="+entityName+"&extId="+extId+"&client=true&status=ENABLED&profiles=External";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        CheckUserResponse checkUserResponse = mapper.readValue(response.getBody(), CheckUserResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful.");
            System.out.println(response.getBody());
            System.out.println("User created!");
            grId = checkUserResponse.getId();
            parentId = checkUserResponse.getParentId();
            entityName = checkUserResponse.getName();
            addedUser = true;
            setGRUser(entityName, grId, extId);
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }
        return response;
    }

    public ResponseEntity<String> loginUser(int playerId ) throws JsonProcessingException {


        HttpEntity<Object> entity = addHeaders();

        String url = "https://api-int.virtustec.com:8383/api/external/v2/session/login?accountId="+playerId+"&userId="+playerId;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        LoginResponse loginResponse = mapper.readValue(response.getBody(), LoginResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Request Successful.");
            System.out.println(response.getBody());
            System.out.println("Logging Successful!");
            onlineHash = loginResponse.getOnlineHash();
        } else {
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }

        return response;
    }


    // Util Functions
    public void setGRUser (String username, int grId, int extId){
    try {
        if(!grUserExist(grId)){
            GRUser grUser = new GRUser();
            grUser.setUsername(username);
            grUser.setExtId(extId);
            grUser.setGrId((long) grId);
            grUserRepository.save(grUser);
        }
        else{
            System.out.println("GR User exists in DB");
        }
    }catch (Exception e){
        e.printStackTrace();
    }

    }

    public boolean grUserExist (int grId){
        System.out.println(grUserRepository.existsByGrId((long) grId));
        return grUserRepository.existsByGrId((long) grId);
    }

    public HttpEntity addHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("apiId", apiId);
        headers.add("apiHash", apiHash);
        headers.add("apiDomain", apiDomain);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        return entity;
    }
}
