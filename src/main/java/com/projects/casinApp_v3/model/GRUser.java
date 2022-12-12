package com.projects.casinApp_v3.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gr_users")
@Data
@NoArgsConstructor
public class GRUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    Long grId;
    @Column
    int extId;
    @Column
    String username;
    @Column
    Long walletId;

}
