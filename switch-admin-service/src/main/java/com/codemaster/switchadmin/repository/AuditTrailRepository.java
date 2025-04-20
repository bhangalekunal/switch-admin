package com.codemaster.switchadmin.repository;

import com.codemaster.switchadmin.entity.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, String> {
}
