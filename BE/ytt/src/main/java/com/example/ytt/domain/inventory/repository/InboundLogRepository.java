package com.example.ytt.domain.inventory.repository;

import com.example.ytt.domain.inventory.domain.InboundLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboundLogRepository extends JpaRepository<InboundLog, Long>, InboundLogRepositoryCustom {

}