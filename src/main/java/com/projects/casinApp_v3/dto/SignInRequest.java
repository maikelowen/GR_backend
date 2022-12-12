package com.projects.casinApp_v3.dto;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}