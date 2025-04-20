package com.codemaster.switchadmin.service;

import com.codemaster.switchadmin.entity.AppConfig;
import com.codemaster.switchadmin.entity.AuditTrail;
import com.codemaster.switchadmin.repository.AuditTrailRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        // Get current HTTP request to extract session info
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())
                .getRequest();

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
                .ipAddress(getClientIpAddress(request))
                .userAgent(request.getHeader("User-Agent"))
                .authMethod(determineAuthMethod(request))
                .build();

        auditTrailRepository.save(audit);
    }

    public void logConfigChange(String userId,
                                String userName,
                                AuditTrail.ActionType actionType,
                                String configId,
                                AppConfig oldConfig,
                                AppConfig newConfig,
                                String ipAddress,
                                String userAgent,
                                AuditTrail.AuthMethod authMethod) {


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
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .authMethod(authMethod)
                .build();

        auditTrailRepository.save(audit);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());
        return ip.split(",")[0]; // In case of multiple IPs (proxies)
    }

    private AuditTrail.AuthMethod determineAuthMethod(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;

        if (authHeader.startsWith("Bearer ")) {
            return AuditTrail.AuthMethod.JWT;
        } else if (authHeader.startsWith("Basic ")) {
            return AuditTrail.AuthMethod.BASIC;
        } else if (authHeader.contains("oauth_token")) {
            return AuditTrail.AuthMethod.OAUTH2;
        } else if (authHeader.contains("apikey")) {
            return AuditTrail.AuthMethod.API_KEY;
        }
        return null;
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
