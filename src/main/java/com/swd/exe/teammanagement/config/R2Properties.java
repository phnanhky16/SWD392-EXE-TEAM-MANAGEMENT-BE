package com.swd.exe.teammanagement.config;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "r2")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class R2Properties {

    @NotBlank
    String accountId;

    @NotBlank
    String accessKeyId;

    @NotBlank
    String secretAccessKey;

    @NotBlank
    String bucketName;

    @NotBlank
    String publicBaseUrl;
}
