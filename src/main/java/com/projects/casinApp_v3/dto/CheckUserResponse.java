package com.projects.casinApp_v3.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class CheckUserResponse {
    public ArrayList<Object> entityType;
    public int id;
    public String name;
    public String extId;
    public String extData;
    public int parentId;
    public int numChildren;
    public String status;
    public String inheritedStatus;
    public boolean hasChildren;
    public boolean isCustomProfile;
    public boolean isClient;
    public ArrayList<String> profiles;
}
