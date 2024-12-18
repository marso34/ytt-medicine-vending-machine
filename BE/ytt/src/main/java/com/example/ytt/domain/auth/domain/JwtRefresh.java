package com.example.ytt.domain.auth.domain;

import com.example.ytt.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "refresh_token")
public class JwtRefresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String refresh;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiration;

}
