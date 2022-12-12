package com.projects.casinApp_v3.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
public class GRWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long intWalletId;

    @Column
    private Long grWalletId;

    @Column
    private Long grID;

    @Column
    private String username;

    @Column
    private BigDecimal credit;

    @Column
    private int extId;


}
