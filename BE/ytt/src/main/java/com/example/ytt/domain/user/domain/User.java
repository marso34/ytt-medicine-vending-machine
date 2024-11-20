package com.example.ytt.domain.user.domain;

import com.example.ytt.domain.management.domain.Management;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.favorite.domain.Favorite;
import com.example.ytt.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Email
    @Column(nullable = false, unique = true)
    private String email; // 이메일 형식

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true, unique = true)
    private String phoneNumber;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // orphanRemoval = true
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // orphanRemoval = true
    private List<Management> managements;

    @Builder
    public User(String email, String password, String name, String phoneNumber, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
