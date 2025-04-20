package com.codemaster.switchadmin.repository;

import com.codemaster.switchadmin.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppConfigRepository extends JpaRepository<AppConfig, String> {
    Optional<AppConfig> findByConfigKey(String configKey);
    boolean existsByConfigKey(String configKey);
}
