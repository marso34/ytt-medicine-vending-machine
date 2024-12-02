package com.example.ytt.domain.user.repository;

import com.example.ytt.domain.user.domain.JwtRefresh;
import com.example.ytt.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface RefreshRepository extends JpaRepository<JwtRefresh, Long> {

    Boolean existsByRefresh(String refresh);

    List<JwtRefresh> findAllByExpirationBefore(Date date);

    @Transactional
    void deleteByRefresh(String refresh);

    @Transactional
    void deleteByUser(User user);
}
