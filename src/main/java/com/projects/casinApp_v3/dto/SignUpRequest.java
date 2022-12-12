package com.projects.casinApp_v3.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    private int extId;
}