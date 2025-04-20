package com.codemaster.switchadmin.entity;

import com.codemaster.switchadmin.entity.generator.StringPrefixedSequenceIdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "APP_CONFIG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AppConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_CONFIG_SEQ")
    @GenericGenerator(
            name = "APP_CONFIG_SEQ",
            strategy = "com.codemaster.switchadmin.entity.generator.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.SEQUENCE_PARAM, value = "APP_CONFIG_SEQ"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INITIAL_PARAM, value = "1"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "CONF"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%08d")
            }
    )
    @EqualsAndHashCode.Include
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
