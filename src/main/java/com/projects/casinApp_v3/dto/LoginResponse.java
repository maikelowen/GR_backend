package com.projects.casinApp_v3.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
@Data
@NoArgsConstructor
public class LoginResponse {

    public Unit unit;
    public Staff staff;
    public String onlineHash;


    public class Staff{
        public ArrayList<Object> entityType;
        public int id;
        public String name;
        public String extId;
        public String extData;
        public int parentId;
        public String status;
        public String inheritedStatus;
        public boolean hasChildren;
        public boolean isCustomProfile;
        public boolean isClient;
        public ArrayList<String> profiles;
    }

    public class Unit{
        public ArrayList<Object> entityType;
        public int id;
        public String name;
        public String extId;
        public String extData;
        public int parentId;
        public String status;
        public String inheritedStatus;
        public boolean hasChildren;
        public boolean isCustomProfile;
        public boolean isClient;
        public ArrayList<String> profiles;
    }
}
