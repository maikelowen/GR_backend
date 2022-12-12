package com.projects.casinApp_v3.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class CreateWalletResponse {
    public int id;
    public String extId;
    public int entityId;
    public String entityName;
    public int priority;
    public String currency;
    public Date startDate;
    public Date lastModification;
    public String status;
    public boolean isPromotion;
    public double balance;
}
