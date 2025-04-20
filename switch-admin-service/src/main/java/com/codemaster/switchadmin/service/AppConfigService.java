package com.codemaster.switchadmin.service;

import com.codemaster.switchadmin.dto.AppConfigRequest;
import com.codemaster.switchadmin.dto.AppConfigResponse;
import com.codemaster.switchadmin.entity.AppConfig;
import com.codemaster.switchadmin.entity.AuditTrail;
import com.codemaster.switchadmin.entity.UserAccount;
import com.codemaster.switchadmin.exception.AppConfigAlreadyExistsException;
import com.codemaster.switchadmin.exception.AppConfigNotFoundException;
import com.codemaster.switchadmin.exception.UserAccountNotFoundException;
import com.codemaster.switchadmin.repository.AppConfigRepository;
import com.codemaster.switchadmin.repository.AuditTrailRepository;
import com.codemaster.switchadmin.repository.UserAccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppConfigService {

    private final AppConfigRepository appConfigRepository;
    private final AuditService auditService;
    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    public AppConfigService(AppConfigRepository appConfigRepository, AuditService auditService,
                            UserAccountRepository userAccountRepository, ModelMapper modelMapper) {
        this.appConfigRepository = appConfigRepository;
        this.auditService = auditService;
        this.userAccountRepository = userAccountRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<AppConfigResponse> getAllConfigs() {
        return appConfigRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppConfigResponse getConfigById(String configId) {
        AppConfig config = appConfigRepository.findById(configId)
                .orElseThrow(() -> new AppConfigNotFoundException("AppConfig not found with id: " + configId));
        return convertToDto(config);
    }

    @Transactional(readOnly = true)
    public AppConfigResponse getConfigByKey(String configKey) {
        AppConfig config = appConfigRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new AppConfigNotFoundException("AppConfig not found with key: " + configKey));
        return convertToDto(config);
    }


    @Transactional
    public AppConfigResponse createConfig(AppConfigRequest request, String email) {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UserAccountNotFoundException("UserAccount not found with email: " + email));

        if (appConfigRepository.existsByConfigKey(request.getConfigKey())) {
            throw new AppConfigAlreadyExistsException("AppConfig already exists with key: " + request.getConfigKey());
        }

        AppConfig config = AppConfig.builder()
                .configKey(request.getConfigKey())
                .configValue(request.getConfigValue())
                .description(request.getDescription())
                .active(request.isActive())
                .build();

        AppConfig savedConfig = appConfigRepository.save(config);

        // Audit the creation
        auditService.logConfigChange(
                userAccount.getUserId(),
                userAccount.getFullName(),
                AuditTrail.ActionType.CREATE,
                savedConfig.getConfigId(),
                null,
                savedConfig
        );

        return convertToDto(savedConfig);
    }

    @Transactional
    public AppConfigResponse updateConfig(String configId, AppConfigRequest request, String email) {
        AppConfig existingConfig = appConfigRepository.findById(configId)
                .orElseThrow(() -> new AppConfigNotFoundException("AppConfig not found with id: " + configId));

        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UserAccountNotFoundException("UserAccount not found with email: " + email));

        // Check if updating to a key that already exists (and is different)
        if (!existingConfig.getConfigKey().equals(request.getConfigKey()) &&
                appConfigRepository.existsByConfigKey(request.getConfigKey())) {
            throw new AppConfigAlreadyExistsException("AppConfig already exists with key: " + request.getConfigKey());
        }

        // Save old values for audit
        AppConfig oldConfig = modelMapper.map(existingConfig, AppConfig.class);

        existingConfig.setConfigKey(request.getConfigKey());
        existingConfig.setConfigValue(request.getConfigValue());
        existingConfig.setDescription(request.getDescription());
        existingConfig.setActive(request.isActive());

        AppConfig updatedConfig = appConfigRepository.save(existingConfig);

        // Audit the update
        auditService.logConfigChange(
                userAccount.getUserId(),
                userAccount.getFullName(),
                AuditTrail.ActionType.UPDATE,
                updatedConfig.getConfigId(),
                oldConfig,
                updatedConfig
        );

        return convertToDto(updatedConfig);
    }


    @Transactional
    public void deleteConfig(String configId, String email) {
        AppConfig config = appConfigRepository.findById(configId)
                .orElseThrow(() -> new AppConfigNotFoundException("AppConfig not found with id: " + configId));

        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UserAccountNotFoundException("UserAccount not found with email: " + email));

        config.setActive(false);
        appConfigRepository.save(config);


        // Audit the deletion
        auditService.logConfigChange(
                userAccount.getUserId(),
                userAccount.getFullName(),
                AuditTrail.ActionType.DELETE,
                configId,
                config,
                null
        );
    }

    @Transactional
    public AppConfigResponse toggleConfigStatus(String configId, boolean active, String email) {
        AppConfig config = appConfigRepository.findById(configId)
                .orElseThrow(() -> new AppConfigNotFoundException("AppConfig not found with id: " + configId));

        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UserAccountNotFoundException("UserAccount not found with email: " + email));

        // Save old values for audit
        AppConfig oldConfig = modelMapper.map(config, AppConfig.class);

        config.setActive(active);
        AppConfig updatedConfig = appConfigRepository.save(config);

        // Audit the status change
        auditService.logConfigChange(
                userAccount.getUserId(),
                userAccount.getFullName(),
                AuditTrail.ActionType.UPDATE,
                configId,
                oldConfig,
                updatedConfig
        );

        return convertToDto(updatedConfig);
    }

    private AppConfigResponse convertToDto(AppConfig config) {
        return modelMapper.map(config, AppConfigResponse.class);
    }
}
