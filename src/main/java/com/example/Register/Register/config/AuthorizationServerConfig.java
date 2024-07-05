package com.example.Register.Register.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
    @Autowired
    public PasswordEncoder passwordEncoder;

}
