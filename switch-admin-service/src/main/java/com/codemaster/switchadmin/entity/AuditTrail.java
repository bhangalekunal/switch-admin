package com.codemaster.switchadmin.entity;

import com.codemaster.switchadmin.entity.generator.StringPrefixedSequenceIdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.time.LocalDateTime;

@Entity
@Table(name = "AUDIT_TRAIL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUDIT_TRAIL_SEQ")
    @GenericGenerator(
            name = "AUDIT_TRAIL_SEQ",
            strategy = "com.codemaster.switchadmin.entity.generator.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.SEQUENCE_PARAM, value = "AUDIT_TRAIL_SEQ"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INITIAL_PARAM, value = "1"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "ADTL"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%08d")
            }
    )
    @EqualsAndHashCode.Include
    @Column(name = "AUDIT_ID", length = 12, updatable = false)
    private String auditId;

    @Column(name = "USER_ID", length = 12, nullable = false)
    private String userId;

    @Column(name = "USER_NAME", length = 100, nullable = false)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION_TYPE", length = 50, nullable = false)
    private ActionType actionType;

    @Column(name = "TARGET_ENTITY", length = 100, nullable = false)
    private String targetEntity;

    @Column(name = "TARGET_ENTITY_ID", length = 12)
    private String targetEntityId;

    @Lob
    @Column(name = "OLD_VALUE")
    private String oldValue;

    @Lob
    @Column(name = "NEW_VALUE")
    private String newValue;

    @Lob
    @Column(name = "ACTION_DESCRIPTION", nullable = false)
    private String actionDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION_STATUS", length = 20, nullable = false)
    private ActionStatus actionStatus;

    @Lob
    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Column(name = "IP_ADDRESS", length = 45)
    private String ipAddress;

    @Lob
    @Column(name = "USER_AGENT")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "AUTH_METHOD", length = 20)
    private AuthMethod authMethod;

    @Column(name = "ACTION_TIMESTAMP", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime actionTimestamp;

    @Column(name = "IS_SENSITIVE", nullable = false)
    @Builder.Default
    private boolean isSensitive = true;

    public enum ActionType {
        CREATE, UPDATE, DELETE, LOGIN, LOGOUT, ACCESS, SYSTEM_EVENT
    }

    public enum ActionStatus {
        SUCCESS, FAILURE, PARTIAL, DENIED
    }

    public enum AuthMethod {
        JWT, OAUTH2, BASIC, API_KEY
    }
}
