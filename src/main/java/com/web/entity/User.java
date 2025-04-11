package com.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.web.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String username;

    private String email;

    private String password;

    private String fullname;

    private String phone;

    private Boolean actived;

    private String activation_key;

    private Date createdDate;

    private String tokenFcm;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ManyToOne
    @JoinColumn(name = "authority_name")
    private Authority authorities;
}

