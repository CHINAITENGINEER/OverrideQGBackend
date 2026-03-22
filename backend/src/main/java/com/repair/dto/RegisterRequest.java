package com.repair.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String account;
    private String password;
    private Integer role;
}
