package com.codemaster.switchadmin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "APP_CONFIG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AppConfig {
    @Id
    @EqualsAndHashCode.Include // Include only ID in equals/hashCode
    @Column(name = "CONFIG_ID", length = 12, updatable = false)
    private String configId;

    @Column(name = "CONFIG_KEY", nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(name = "CONFIG_VALUE", nullable = false, length = 500)
    @Nationalized
    private String configValue;

    @Column(name = "DESCRIPTION", length = 1000)
    @Nationalized
    private String description;

    @Column(name = "IS_ACTIVE", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "CREATED_AT", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isSystemCritical() {
        return configKey != null &&
                (configKey.startsWith("system.") ||
                        configKey.startsWith("security."));
    }
}
