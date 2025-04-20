package com.codemaster.switchadmin.service;

import com.codemaster.switchadmin.entity.AppConfig;
import com.codemaster.switchadmin.entity.AuditTrail;
import com.codemaster.switchadmin.repository.AuditTrailRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuditService {
    private final AuditTrailRepository auditTrailRepository;

    public AuditService(AuditTrailRepository auditTrailRepository) {
        this.auditTrailRepository = auditTrailRepository;
    }

    public void logConfigChange(String userId,
                                String userName,
                                AuditTrail.ActionType actionType,
                                String configId,
                                AppConfig oldConfig,
                                AppConfig newConfig) {

        AuditTrail audit = AuditTrail.builder()
                .userId(userId)
                .userName(userName)
                .actionType(actionType)
                .targetEntity("APP_CONFIG")
                .targetEntityId(configId)
                .oldValue(serializeConfig(oldConfig))
                .newValue(serializeConfig(newConfig))
                .actionDescription(getActionDescription(actionType, configId))
                .actionStatus(AuditTrail.ActionStatus.SUCCESS)
                .isSensitive(isConfigSensitive(newConfig != null ? newConfig : oldConfig))
                .build();

        auditTrailRepository.save(audit);
    }

    private String serializeConfig(AppConfig config) {
        if (config == null) return null;

        Map<String, Object> configMap = new HashMap<>();
        configMap.put("configKey", config.getConfigKey());
        configMap.put("configValue", isConfigSensitive(config) ? "*****" : config.getConfigValue());
        configMap.put("description", config.getDescription());
        configMap.put("active", config.isActive());

        return configMap.toString();
    }

    private boolean isConfigSensitive(AppConfig config) {
        return config != null &&
                (config.getConfigKey().contains("password") ||
                        config.getConfigKey().contains("secret") ||
                        config.getConfigKey().contains("token"));
    }

    private String getActionDescription(AuditTrail.ActionType actionType, String configId) {
        return String.format("AppConfig %s for config ID: %s",
                actionType.toString().toLowerCase(),
                configId);
    }
}
