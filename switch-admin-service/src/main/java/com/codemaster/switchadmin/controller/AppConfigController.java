package com.codemaster.switchadmin.controller;

import com.codemaster.switchadmin.dto.AppConfigRequest;
import com.codemaster.switchadmin.dto.AppConfigResponse;
import com.codemaster.switchadmin.service.AppConfigService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/app-configs")
public class AppConfigController {
    private final AppConfigService appConfigService;

    public AppConfigController(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    @PreAuthorize("@permissionChecker.hasAllPermissions(authentication, 'CONFIG_READ')")
    @GetMapping
    public ResponseEntity<List<AppConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok(appConfigService.getAllConfigs());
    }

    @PreAuthorize("@permissionChecker.hasAllPermissions(authentication, 'CONFIG_READ')")
    @GetMapping("/{configId}")
    public ResponseEntity<AppConfigResponse> getConfigById(@PathVariable String configId) {
        return ResponseEntity.ok(appConfigService.getConfigById(configId));
    }

    @PreAuthorize("@permissionChecker.hasAllPermissions(authentication, 'CONFIG_READ')")
    @GetMapping("/key/{configKey}")
    public ResponseEntity<AppConfigResponse> getConfigByKey(@PathVariable String configKey) {
        return ResponseEntity.ok(appConfigService.getConfigByKey(configKey));
    }

    @PreAuthorize("@permissionChecker.hasAllPermissions(authentication, 'CONFIG_CREATE')")
    @PostMapping
    public ResponseEntity<AppConfigResponse> createConfig(
            @Valid @RequestBody AppConfigRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        AppConfigResponse response = appConfigService.createConfig(
                request,
                userDetails.getUsername()
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{configId}")
                .buildAndExpand(response.getConfigId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PreAuthorize("@permissionChecker.hasAllPermissions(authentication, 'CONFIG_UPDATE')")
    @PutMapping("/{configId}")
    public ResponseEntity<AppConfigResponse> updateConfig(
            @PathVariable String configId,
            @Valid @RequestBody AppConfigRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(appConfigService.updateConfig(
                configId,
                request,
                userDetails.getUsername()
        ));
    }

    @PreAuthorize("@permissionChecker.hasAllPermissions(authentication, 'CONFIG_STATUS_UPDATE')")
    @PatchMapping("/{configId}/status")
    public ResponseEntity<AppConfigResponse> toggleConfigStatus(
            @PathVariable String configId,
            @RequestParam boolean active,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(appConfigService.toggleConfigStatus(
                configId,
                active,
                userDetails.getUsername()
        ));
    }

    @PreAuthorize("@permissionChecker.hasAllPermissions(authentication, 'CONFIG_DELETE')")
    @DeleteMapping("/{configId}")
    public ResponseEntity<Void> deleteConfig(
            @PathVariable String configId,
            @AuthenticationPrincipal UserDetails userDetails) {

        appConfigService.deleteConfig(configId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
