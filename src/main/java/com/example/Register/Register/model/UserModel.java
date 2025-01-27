package com.example.Register.Register.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
