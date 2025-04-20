package com.codemaster.switchadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AppConfigRequest {
    @NotBlank
    @Size(max = 100)
    String configKey;

    @NotBlank
    @Size(max = 500)
    String configValue;

    @Size(max = 1000)
    String description;

    boolean active;
}
