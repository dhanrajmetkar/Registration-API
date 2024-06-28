package com.example.Register.Register.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GeneratorType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    @Column(length = 60)
    private String password;
    private String role;
    private Boolean enable=false;

}
